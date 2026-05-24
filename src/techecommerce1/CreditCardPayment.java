package techecommerce1;

/**
 * Concrete implementation of  Payment using credit card.
 * Validates card details and processes the transaction.

 */
public class CreditCardPayment extends Payment {

    // Attributes ──────────────────────────────────────────────
    private String cardNumber;   // stored masked: "****1234"
    private String cardHolder;
    private String expiryDate;   // format: MM/YY
    private String cvv;

    // Constructor ──────────────────────────────────────────────

    public CreditCardPayment(String paymentId, double amount, String paymentDate, String cardNumber, String cardHolder, String expiryDate, String cvv)
    {
        super(paymentId, amount, paymentDate);
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    // Abstract Method Implementations ─────────────────────────
    /**
     * Validates that the card number, expiry, and CVV are not blank

     */
    @Override
    public boolean validatePaymentDetails() {
        if (cardNumber == null || cardNumber.length() != 16)
            throw new IllegalArgumentException("Card number must be 16 digits.");
        if (cvv == null || cvv.length() != 3)
            throw new IllegalArgumentException("CVV must be 3 digits.");
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}"))
            throw new IllegalArgumentException("Expiry date must be in MM/YY format.");
        return true;
    }

    /**
     * Processes the credit card payment if validation passes.

     */
    @Override
    public boolean processPayment() {
        try {
            if (validatePaymentDetails()) {
                setStatus("COMPLETED");
                System.out.println("Credit card payment of $" + getAmount() +
                        " processed successfully for " + cardHolder);
                return true;
            }
        } catch (IllegalArgumentException e) {
            setStatus("FAILED");
            System.out.println("Payment failed: " + e.getMessage());
        }
        return false;
    }

    //  Getters & Setters ────────────────────────────────────────


    public String getMaskedCardNumber() {
        return "****" + cardNumber.substring(12);
    }


    public String getCardHolder() { return cardHolder; }


    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }


    public String getExpiryDate() { return expiryDate; }


    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    //toString ─────────────────────────────────────────────────
    @Override
    public String toString() {
        return "CreditCardPayment{card='" + getMaskedCardNumber() +
                "', holder='" + cardHolder + "', amount=" + getAmount() +
                ", status='" + getStatus() + "'}";
    }
}
