package techecommerce1.domain;

import techecommerce1.application.OrderObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer user of the e-commerce platform.
 * Inherits from User and manages shopping and order history.
 */
// جعل كلاس العميل يطبق واجهة المراقب
public class Customer extends User implements OrderObserver {

    // Attributes ──────────────────────────────────────────────
    private String shippingAddress;
    private List<Order> orderHistory;
    private ShoppingCart cart;

    //Constructor ──────────────────────────────────────────────
    public Customer(){

    }
    public Customer(String userId, String name, String email,
                    String password, String phoneNumber, String shippingAddress) {
        super(userId, name, email, password, phoneNumber);
        this.shippingAddress = shippingAddress;
        this.orderHistory = new ArrayList<>();
        this.cart  = new ShoppingCart(userId + "_cart");
    }

    // Overridden Method
    /** Returns the role of this user
     */
    @Override
    public String getRole() {
        return "عميل";
    }
    // الأكواد الحالية للعميل دون تغيير...[cite: 14]

    // تنفيذ دالة التحديث لاستقبال الإشعارات تلقائياً
    @Override
    public void update(String trackingId, String newStatus) {
        System.out.println("Notification to Customer [" + getName() + "]: Your shipment " + trackingId + " status has changed to: " + newStatus);
    }

    // Methods ──────────────────────────────────────────────────
    /**
     * Places an order from the current shopping cart.
     */

    // تعديل دالة عمل طلب في كلاس Customer لاستقبال وسيلة الدفع ديناميكياً
    public Order placeOrder(String orderId, String paymentDate, PaymentStrategy paymentStrategy) {
        if (cart.getItems().isEmpty())
            throw new IllegalStateException("Cannot place order: shopping cart is empty.");

        double total = cart.calculateTotal();

        // تنفيذ عملية الدفع بناءً على الاستراتيجية الممررة
        boolean paymentSuccess = paymentStrategy.processPayment(total);

        if (!paymentSuccess) {
            throw new IllegalStateException("Order creation failed due to payment failure.");
        }

        Order order = new Order(orderId, getUserId(), total, paymentDate, shippingAddress);
        orderHistory.add(order);
        cart.clearCart();
        System.out.println("Order placed successfully: " + orderId);
        return order;
    }

    /**
     * Adds a product to the shopping cart */
    public void addToCart(Product product, int quantity) {
        cart.addItem(product, quantity);
    }


    // Getters & Setters


    public String getShippingAddress() { return shippingAddress; }


    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    public List<Order> getOrderHistory() { return orderHistory; }


    public ShoppingCart getCart() { return cart; }

    // toString

    @Override
    public String toString() {
        return "Customer{id='" + getUserId() + "', name='" + getName() +
                "', address='" + shippingAddress + "'}";
    }
}
