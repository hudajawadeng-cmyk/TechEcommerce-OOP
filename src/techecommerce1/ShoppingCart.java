package techecommerce1;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a customer's shopping cart.
 * Holds selected products and calculates the total before checkout
 */
public class ShoppingCart {

    // Attributes ──────────────────────────────────────────────

    private String cartId;
    // Map: Product → quantity
    private Map<Product, Integer> items;

    //Constructor ───────────────────────────────────

    /**Constructs an empty ShoppingCart
     */
    public ShoppingCart(String cartId) {
        this.cartId = cartId;
        this.items  = new HashMap<>();
    }

    // Methods ─────────────────────────────────
    /**
     * Adds a product to the cart with the specified quantity

     */
    public void addItem(Product product, int quantity) {
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive.");
        if (!product.isAvailable())
            throw new IllegalStateException("Product '" + product.getName() + "' is out of stock.");

        items.merge(product, quantity, Integer::sum);
        System.out.println("Added " + quantity + "x " + product.getName() + " to cart.");
    }

    /** Removes a product from the cart entirely
     */
    public void removeItem(String productId) {
        items.entrySet().removeIf(e -> e.getKey().getProductId().equals(productId));
    }

    /**
     * Calculates the total price of all items in the cart
     */
    public double calculateTotal() {
        return items.entrySet().stream().mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    /**  Clears all items from the cart after a successful order.
     */
    public void clearCart() {
        items.clear();
        System.out.println("Cart cleared.");
    }

    /** Returns the number of distinct products in the cart
     */
    public int getItemCount() {
        return items.size();
    }

    // Getters ──────────────────────────────────────────────────

    public String getCartId() { return cartId; }

    public Map<Product, Integer> getItems() { return items; }

    // toString ─────────────────────────────────────────────────
    @Override
    public String toString() {
        return "ShoppingCart{id='" + cartId + "', items=" + items.size() +
                ", total=" + calculateTotal() + "}";
    }
}
