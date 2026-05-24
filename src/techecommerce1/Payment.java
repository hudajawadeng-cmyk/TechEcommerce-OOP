package techecommerce1;

/**
 * Abstract class representing a payment method.
 * Defines the common structure for all payment types
 */
public abstract class Payment {

    // Attributes ──────────────────────────────────────────

    private String paymentId;
    private double amount;
    private String paymentDate;
    private String status; // "PENDING", "COMPLETED", "FAILED"

    // Constructor ──────────────────────────────────────────────

    public Payment(String paymentId, double amount, String paymentDate) {
        this.paymentId   = paymentId;
        this.amount      = amount;
        this.paymentDate = paymentDate;
        this.status      = "PENDING";
    }

    // Abstract Methods ─────────────────────────────────────────
    /**
     * Processes the payment. Must be implemented by subclasses
     */
    public abstract boolean processPayment();

    /**
     * Validates payment details. Must be implemented by subclasses.
     */
    public abstract boolean validatePaymentDetails();

    //Getters & Setters ────────────────────────────────────────

    public String getPaymentId() { return paymentId; }


    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }


    public double getAmount() { return amount; }


    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
        this.amount = amount;
    }


    public String getPaymentDate() { return paymentDate; }


    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }


    public String getStatus() { return status; }


    public void setStatus(String status) { this.status = status; }

    //toString ─────────────────────────────────────────────
    @Override
    public String toString() {
        return "Payment{id='" + paymentId + "', amount=" + amount +
                ", date='" + paymentDate + "', status='" + status + "'}";
    }
}
