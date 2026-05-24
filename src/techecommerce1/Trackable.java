package techecommerce1;

/**

 Interface for entities that can be tracked (orders, shipments).
 Any class implementing this must provide tracking functionality.

 */
public interface Trackable {

    /**
     * Returns the current tracking status.
     return status as a String
     */
    String getTrackingStatus();

    /**
     * Updates the tracking status.
     parameter status the new status

     */
    void updateStatus(String status);

    /**
     * Returns the tracking ID.

     */
    String getTrackingId();
}
