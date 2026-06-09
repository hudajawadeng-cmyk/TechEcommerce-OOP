package techecommerce1.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager — Singleton JDBC connection manager.
 *
 * Provides all SQL operations used by the GUI Frames:
 *   - LoginFrame        → authenticateUser()
 *   - ProductListFrame  → getProducts(), searchProducts(), deleteProduct(), addProduct()
 *   - ShoppingCartFrame → getCart(), addToCart(), removeFromCart(), updateCartQty(),
 *                         clearCart(), getPromoCode(), placeOrder()
 *   - OrdersFrame       → getOrders(), cancelOrder()
 *   - ReviewsFrame      → getReviews(), addReview(), markHelpful()
 *   - ProfileFrame      → getUserProfile(), updateProfile(), changePassword()
 *   - InventoryFrame    → getInventory(), updateStock()
 *   - TrackShipmentFrame→ getShipmentsByCustomer(), getShipmentEvents()
 *
 * Setup:
 *   1. Add mysql-connector-j-8.x.x.jar to your project classpath.
 *   2. Change DB_URL / DB_USER / DB_PASS below to match your MySQL server.
 *   3. Run techecommerce_schema.sql once in MySQL Workbench to create the DB.
 */
public class DatabaseManager {

    // ── Connection settings — change these to match your MySQL ──
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/techecommerce"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";   // ← change this

    // ── Singleton ────────────────────────────────────────────────
    private static DatabaseManager instance;
    private Connection conn;

    public DatabaseManager() {
        connect();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // ── Connection ───────────────────────────────────────────────
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("[DB] Connected to MySQL successfully.");
        } catch (Exception e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
    }

    /** Returns true if the connection is alive. */
    public boolean isConnected() {
        try { return conn != null && !conn.isClosed(); }
        catch (SQLException e) { return false; }
    }

    public void close() {
        try { if (conn != null) conn.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  LoginFrame                                              ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Authenticates a user by email and password.
     * @return String[3] = {user_id, name, role}  or null if not found.
     */
    public String[] authenticateUser(String email, String password) {
        String sql = "SELECT user_id, name, role, password FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (dbPassword != null && dbPassword.equals(password.trim())) {
                    return new String[]{
                            rs.getString("user_id"),
                            rs.getString("name"),
                            rs.getString("role")
                    };
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  ProductListFrame + AddProductFrame                      ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns all products joined with their category name.
     * Each row: {product_id, name, brand, price, category, stock, status}
     */
    public List<Object[]> getProducts() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.brand, p.price, "
                + "COALESCE(c.name,'Other') AS category, p.stock_quantity "
                + "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id "
                + "ORDER BY p.product_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int stock = rs.getInt("stock_quantity");
                String status = stock == 0 ? "Out of Stock" : stock <= 5 ? "Low Stock" : "In Stock";
                list.add(new Object[]{
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getString("brand"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        stock,
                        status
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Searches products by keyword (name, brand, or category).
     */
    public List<Object[]> searchProducts(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.brand, p.price, "
                + "COALESCE(c.name,'Other') AS category, p.stock_quantity "
                + "FROM products p LEFT JOIN categories c ON p.category_id = c.category_id "
                + "WHERE p.name LIKE ? OR p.brand LIKE ? OR c.name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int stock = rs.getInt("stock_quantity");
                String status = stock == 0 ? "Out of Stock" : stock <= 5 ? "Low Stock" : "In Stock";
                list.add(new Object[]{
                        rs.getString("product_id"), rs.getString("name"),
                        rs.getString("brand"),      rs.getDouble("price"),
                        rs.getString("category"),   stock, status
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Adds a new product (Admin). Returns true on success.
     */
    public boolean addProduct(String id, String name, String brand,
                              double price, String specs, String categoryName, int stock) {
        try {
            // Resolve category_id from name
            String catId = getCategoryId(categoryName);
            String sql = "INSERT INTO products(product_id,name,brand,price,specifications,category_id,stock_quantity)"
                    + " VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);   ps.setString(2, name);   ps.setString(3, brand);
            ps.setDouble(4, price); ps.setString(5, specs);  ps.setString(6, catId);
            ps.setInt(7, stock);
            ps.executeUpdate();

            // Add inventory row
            String invId = "INV" + System.currentTimeMillis();
            PreparedStatement inv = conn.prepareStatement(
                    "INSERT INTO inventory(inventory_id,product_id,quantity,minimum_stock_level) VALUES(?,?,?,5)");
            inv.setString(1, invId); inv.setString(2, id); inv.setInt(3, stock);
            inv.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Deletes a product by ID (Admin). Returns true on success.
     */
    public boolean deleteProduct(String productId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM products WHERE product_id=?")) {
            ps.setString(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private String getCategoryId(String name) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT category_id FROM categories WHERE name=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getString(1);
        return "CAT01"; // default fallback
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  ShoppingCartFrame                                       ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns cart items for a customer.
     * Each row: {product_id, name, unit_price, qty, subtotal}
     */
    public List<Object[]> getCart(String customerId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT c.product_id, p.name, p.price, c.quantity "
                + "FROM cart c JOIN products p ON c.product_id=p.product_id "
                + "WHERE c.customer_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("price");
                int qty      = rs.getInt("quantity");
                list.add(new Object[]{
                        rs.getString("product_id"), rs.getString("name"),
                        price, qty, Math.round(price * qty * 100.0) / 100.0
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Adds a product to the cart (or increments qty if already exists).
     */
    public boolean addToCart(String customerId, String productId, int qty) {
        String sql = "INSERT INTO cart(customer_id,product_id,quantity) VALUES(?,?,?) "
                + "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId); ps.setString(2, productId);
            ps.setInt(3, qty);           ps.setInt(4, qty);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Updates quantity of a cart item.
     */
    public boolean updateCartQty(String customerId, String productId, int newQty) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE cart SET quantity=? WHERE customer_id=? AND product_id=?")) {
            ps.setInt(1, newQty); ps.setString(2, customerId); ps.setString(3, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Removes a single item from cart.
     */
    public boolean removeFromCart(String customerId, String productId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart WHERE customer_id=? AND product_id=?")) {
            ps.setString(1, customerId); ps.setString(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Clears the entire cart for a customer.
     */
    public boolean clearCart(String customerId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cart WHERE customer_id=?")) {
            ps.setString(1, customerId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Validates a promo code.
     * Returns double[2] = {discount_type_flag(0=percent,1=fixed), discount_value}
     * or null if invalid / expired.
     */
    public double[] getPromoCode(String code) {
        String sql = "SELECT discount_type, discount_value FROM promo_codes "
                + "WHERE code=? AND is_active=TRUE AND (expiry_date IS NULL OR expiry_date >= CURDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double flag = rs.getString("discount_type").equals("PERCENT") ? 0 : 1;
                return new double[]{ flag, rs.getDouble("discount_value") };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Places an order from cart contents, then clears the cart.
     * Returns the new order_id or null on failure.
     */
    public String placeOrder(String customerId, double total, String address) {
        try {
            conn.setAutoCommit(false);
            String orderId = "ORD-" + System.currentTimeMillis();

            // Insert order
            PreparedStatement o = conn.prepareStatement(
                    "INSERT INTO orders(order_id,customer_id,total_price,order_date,tracking_status,shipping_address,payment_status)"
                            + " VALUES(?,?,?,CURDATE(),'Processing',?,'Pending')");
            o.setString(1,orderId); o.setString(2,customerId);
            o.setDouble(3,total);   o.setString(4,address);
            o.executeUpdate();

            // Insert order items from cart
            List<Object[]> cart = getCart(customerId);
            PreparedStatement oi = conn.prepareStatement(
                    "INSERT INTO order_items(order_id,product_id,quantity,unit_price) VALUES(?,?,?,?)");
            for (Object[] row : cart) {
                oi.setString(1, orderId);
                oi.setString(2, (String) row[0]);
                oi.setInt(3,    (int)    row[3]);
                oi.setDouble(4, (double) row[2]);
                oi.addBatch();

                // Reduce stock
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id=?");
                st.setInt(1, (int) row[3]); st.setString(2, (String) row[0]);
                st.executeUpdate();
            }
            oi.executeBatch();

            // Insert payment
            PreparedStatement py = conn.prepareStatement(
                    "INSERT INTO payments(payment_id,order_id,amount,payment_date,status,payment_type)"
                            + " VALUES(?,?,?,CURDATE(),'Pending','CreditCard')");
            py.setString(1,"PAY-"+System.currentTimeMillis());
            py.setString(2,orderId); py.setDouble(3,total);
            py.executeUpdate();

            clearCart(customerId);
            conn.commit();
            conn.setAutoCommit(true);
            return orderId;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  OrdersFrame                                             ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns all orders for a customer.
     * Each row: {order_id, order_date, product_names, total, tracking_status, payment_status}
     */
    public List<Object[]> getOrders(String customerId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT o.order_id, o.order_date, "
                + "GROUP_CONCAT(p.name SEPARATOR ', ') AS products, "
                + "o.total_price, o.tracking_status, o.payment_status "
                + "FROM orders o "
                + "JOIN order_items oi ON o.order_id=oi.order_id "
                + "JOIN products p    ON oi.product_id=p.product_id "
                + "WHERE o.customer_id=? "
                + "GROUP BY o.order_id ORDER BY o.order_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("order_id"),
                        rs.getString("order_date"),
                        rs.getString("products"),
                        rs.getString("total_price"),
                        rs.getString("tracking_status"),
                        rs.getString("payment_status"),
                        "—"   // action placeholder
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Cancels an order (only if Processing or Dispatched).
     */
    public boolean cancelOrder(String orderId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET tracking_status='Cancelled', payment_status='Refunded' "
                        + "WHERE order_id=? AND tracking_status IN ('Processing','Dispatched')")) {
            ps.setString(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  ReviewsFrame                                            ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns all reviews joined with product and customer name.
     * Each row: {product_name, reviewer_email, rating, comment, review_date, helpful_votes}
     */
    public List<Object[]> getReviews() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.name AS product, u.email, r.rating, r.comment, r.review_date, r.helpful_votes "
                + "FROM reviews r "
                + "JOIN products p  ON r.product_id=p.product_id "
                + "JOIN users u     ON r.customer_id=u.user_id "
                + "ORDER BY r.review_date DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("product"),   rs.getString("email"),
                        rs.getInt("rating"),       rs.getString("comment"),
                        rs.getString("review_date"),rs.getInt("helpful_votes")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Adds a new review. Returns true on success.
     */
    public boolean addReview(String customerId, String productId, int rating, String comment) {
        String revId = "REV-" + System.currentTimeMillis();
        String sql = "INSERT INTO reviews(review_id,customer_id,product_id,rating,comment,review_date,helpful_votes)"
                + " VALUES(?,?,?,?,?,CURDATE(),0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,revId); ps.setString(2,customerId); ps.setString(3,productId);
            ps.setInt(4,rating);   ps.setString(5,comment);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Increments helpful_votes for a review.
     */
    public boolean markHelpful(String productName, String reviewerEmail) {
        String sql = "UPDATE reviews r "
                + "JOIN products p ON r.product_id=p.product_id "
                + "JOIN users u    ON r.customer_id=u.user_id "
                + "SET r.helpful_votes = r.helpful_votes + 1 "
                + "WHERE p.name=? AND u.email=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productName); ps.setString(2, reviewerEmail);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  ProfileFrame                                            ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns user profile.
     * Returns String[5] = {name, email, phone, country, address}
     */
    public String[] getUserProfile(String userId) {
        String sql = "SELECT u.name, u.email, u.phone_number, "
                + "COALESCE(c.country,'—') AS country, "
                + "COALESCE(c.shipping_address,'—') AS address "
                + "FROM users u LEFT JOIN customers c ON u.user_id=c.customer_id "
                + "WHERE u.user_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new String[]{
                    rs.getString("name"),    rs.getString("email"),
                    rs.getString("phone_number"), rs.getString("country"),
                    rs.getString("address")
            };
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Updates user profile fields.
     */
    public boolean updateProfile(String userId, String name, String phone,
                                 String country, String address) {
        try {
            PreparedStatement u = conn.prepareStatement(
                    "UPDATE users SET name=?, phone_number=? WHERE user_id=?");
            u.setString(1,name); u.setString(2,phone); u.setString(3,userId);
            u.executeUpdate();

            PreparedStatement c = conn.prepareStatement(
                    "UPDATE customers SET shipping_address=?, country=? WHERE customer_id=?");
            c.setString(1,address); c.setString(2,country); c.setString(3,userId);
            c.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Changes user password. Returns true on success.
     */
    public boolean changePassword(String userId, String newPassword) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET password=? WHERE user_id=?")) {
            ps.setString(1, newPassword); ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  InventoryFrame                                          ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns inventory data for admin.
     * Each row: {product_id, name, brand, category, quantity, min_stock, status, value}
     */
    public List<Object[]> getInventory() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name, p.brand, "
                + "COALESCE(c.name,'Other') AS category, "
                + "i.quantity, i.minimum_stock_level, p.price "
                + "FROM inventory i "
                + "JOIN products p   ON i.product_id=p.product_id "
                + "LEFT JOIN categories c ON p.category_id=c.category_id "
                + "ORDER BY p.product_id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int qty  = rs.getInt("quantity");
                int minS = rs.getInt("minimum_stock_level");
                String status = qty == 0 ? "Out of Stock" : qty <= minS ? "Low Stock" : "OK";
                double value  = rs.getDouble("price") * qty;
                list.add(new Object[]{
                        rs.getString("product_id"), rs.getString("name"),
                        rs.getString("brand"),      rs.getString("category"),
                        qty, minS, status, Math.round(value*100.0)/100.0
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Updates stock quantity for a product (admin edit in table).
     */
    public boolean updateStock(String productId, int newQty) {
        try {
            PreparedStatement p = conn.prepareStatement(
                    "UPDATE products SET stock_quantity=? WHERE product_id=?");
            p.setInt(1,newQty); p.setString(2,productId); p.executeUpdate();

            PreparedStatement i = conn.prepareStatement(
                    "UPDATE inventory SET quantity=? WHERE product_id=?");
            i.setInt(1,newQty); i.setString(2,productId); i.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ╔══════════════════════════════════════════════════════════╗
    // ║  TrackShipmentFrame                                      ║
    // ╚══════════════════════════════════════════════════════════╝

    /**
     * Returns shipments for a customer's orders.
     * Each row: {order_id, tracking_id, carrier, origin, destination, step, status, eta}
     */
    public List<Object[]> getShipmentsByCustomer(String customerId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT o.order_id, s.tracking_id, s.carrier_name, "
                + "s.origin_address, s.destination_address, s.tracking_step, "
                + "s.tracking_status, s.estimated_delivery_date "
                + "FROM orders o JOIN shipping_info s ON o.order_id=s.order_id "
                + "WHERE o.customer_id=? ORDER BY o.order_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("order_id"),       rs.getString("tracking_id"),
                        rs.getString("carrier_name"),   rs.getString("origin_address"),
                        rs.getString("destination_address"), rs.getInt("tracking_step"),
                        rs.getString("tracking_status"),rs.getString("estimated_delivery_date")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Returns timeline events for a tracking ID, newest first.
     * Each row: {event_time, description, location}
     */
    public List<Object[]> getShipmentEvents(String trackingId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT event_time, description, location "
                + "FROM shipping_events WHERE tracking_id=? ORDER BY event_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("event_time"),
                        rs.getString("description"),
                        rs.getString("location")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}