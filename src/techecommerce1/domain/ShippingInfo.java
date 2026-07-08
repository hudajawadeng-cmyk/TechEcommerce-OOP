package techecommerce1.domain;

import techecommerce1.application.OrderObserver;

import java.util.ArrayList;
import java.util.List;

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

    // إضافة قائمة للمراقبين المهتمين بهذا الشحن
    private List<OrderObserver> observers = new ArrayList<>();

    // Constructor ──────────────────────────────────────────────

    public ShippingInfo(){

    }
    public ShippingInfo(String trackingId, String orderId, String destinationAddress,
                        String carrierName, String estimatedDeliveryDate) {
        this.trackingId   = trackingId;
        this.orderId    = orderId;
        this.destinationAddress  = destinationAddress;
        this.carrierName   = carrierName;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.trackingStatus   = "PREPARING";
    }
    // دالتين لإضافة وإزالة المراقبين
    public void attach(OrderObserver observer) {
        if (observer != null) observers.add(observer);
    }

    public void detach(OrderObserver observer) {
        observers.remove(observer);
    }

    // دالة إشعار كافة المراقبين بالتحديث الجديد
    private void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(trackingId, trackingStatus);
        }
    }

    // Trackable Implementation

    @Override
    public String getTrackingStatus() {
        return trackingStatus;
    }


    @Override
    public void updateStatus(String status) { // تعديل الدالة لإطلاق الإشعار تلقائياً
        if (status == null || status.isBlank())
            throw new IllegalArgumentException("Status cannot be empty.");
        this.trackingStatus = status;
        System.out.println("Shipment " + trackingId + " → " + status);

        // إشعار العميل تلقائياً
        notifyObservers();
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
