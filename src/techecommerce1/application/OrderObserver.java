package techecommerce1.application;

public interface OrderObserver {
    void update(String trackingId, String newStatus);
}
