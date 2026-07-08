package techecommerce1.domain;

/**
 * Strategy Interface representing a payment method.
 */
public interface PaymentStrategy {
    boolean processPayment(double amount);
    boolean validatePaymentDetails();
}
