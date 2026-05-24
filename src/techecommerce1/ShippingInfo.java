package techecommerce1;

/**
 * Manages delivery logistics for an order.
 * Implements Trackable to provide shipment tracking */
public class ShippingInfo implements Trackable {

    // ── Attributes ──────────────────────────────────────────────
    private String trackingId;
    private String orderId;
    private String destinationAddress;
    private String carrierName;
    private String trackingStatus;
    private String estimatedDeliveryDate;

    // Constructor ──────────────────────────────────────────────

    public ShippingInfo(String trackingId, String orderId, String destinationAddress,
                        String carrierName, String estimatedDeliveryDate) {
        this.trackingId            = trackingId;
        this.orderId               = orderId;
        this.destinationAddress    = destinationAddress;
        this.carrierName           = carrierName;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.trackingStatus        = "PREPARING";
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
        System.out.println("Shipment " + trackingId + " → " + status);
    }

    @Override
    public String getTrackingId() {
        return trackingId;
    }

    // Getters & Setters
    public String getOrderId() { return orderId; }


    public String getDestinationAddress() { return destinationAddress; }


    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }


    public String getCarrierName() { return carrierName; }


    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }


    public String getEstimatedDeliveryDate() { return estimatedDeliveryDate; }


    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    // toString
    @Override
    public String toString() {
        return "ShippingInfo{trackingId='" + trackingId + "', carrier='" + carrierName +
                "', status='" + trackingStatus + "', ETA='" + estimatedDeliveryDate + "'}";
    }
}
