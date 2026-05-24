package techecommerce1;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer user of the e-commerce platform.
 * Inherits from User and manages shopping and order history.
 */
public class Customer extends User {

    // Attributes ──────────────────────────────────────────────
    private String shippingAddress;
    private List<Order> orderHistory;
    private ShoppingCart cart;

    //Constructor ──────────────────────────────────────────────

    public Customer(String userId, String name, String email,
                    String password, String phoneNumber, String shippingAddress) {
        super(userId, name, email, password, phoneNumber);
        this.shippingAddress = shippingAddress;
        this.orderHistory    = new ArrayList<>();
        this.cart            = new ShoppingCart(userId + "_cart");
    }

    // Overridden Method
    /** Returns the role of this user
     */
    @Override
    public String getRole() {
        return "Customer";
    }

    // Methods ──────────────────────────────────────────────────
    /**
     * Places an order from the current shopping cart.
     */

    public Order placeOrder(String orderId, String paymentDate) {
        if (cart.getItems().isEmpty())
            throw new IllegalStateException("Cannot place order: shopping cart is empty.");

        Order order = new Order(orderId, getUserId(), cart.calculateTotal(),
                paymentDate, shippingAddress);
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
