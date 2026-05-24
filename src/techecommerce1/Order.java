package techecommerce1;

/**
 * Represents a finalized customer order
 * Implements Trackable to support order status tracking
 */
public class Order implements Trackable {

    // ── Attributes ──────────────────────────────────────────────
    private String orderId;
    private String customerId;
    private double totalPrice;
    private String orderDate;
    private String trackingStatus;  // implements Trackable
    private String shippingAddress;

    // Constructor ──────────────────────────────────────────────

    public Order(String orderId, String customerId, double totalPrice,
                 String orderDate, String shippingAddress) {
        this.orderId         = orderId;
        this.customerId      = customerId;
        this.totalPrice      = totalPrice;
        this.orderDate       = orderDate;
        this.shippingAddress = shippingAddress;
        this.trackingStatus  = "PROCESSING";
    }

    // Trackable Implementation

    @Override
    public String getTrackingStatus() {
        return trackingStatus;
    }


    @Override
    public void updateStatus(String status) {
        if (status == null || status.isBlank())
            throw new IllegalArgumentException("Status cannot be empty.");
        this.trackingStatus = status;
        System.out.println("Order " + orderId + " status updated to: " + status);
    }

    @Override
    public String getTrackingId() {
        return orderId;
    }

    // Getters & Setters ────────────────────────────────────

    public String getOrderId() { return orderId; }


    public String getCustomerId() { return customerId; }


    public double getTotalPrice() { return totalPrice; }


    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }


    public String getOrderDate() { return orderDate; }


    public String getShippingAddress() { return shippingAddress; }


    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    // toString ─────────────────
    @Override
    public String toString() {
        return "Order{id='" + orderId + "', customer='" + customerId +
                "', total=" + totalPrice + ", status='" + trackingStatus + "'}";
    }
}
