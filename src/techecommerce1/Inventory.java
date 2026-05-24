package techecommerce1;

/**
 * Manages the stock quantity of a product in the warehouse.
 * Monitors and updates available inventory automatically */

public class Inventory {

    // Attributes
    private String inventoryId;
    private String productId;
    private int quantity;
    private int minimumStockLevel; // alert threshold

    //  Constructor

    public Inventory(String inventoryId, String productId, int quantity, int minimumStockLevel)
    {
        this.inventoryId       = inventoryId;
        this.productId         = productId;
        this.quantity          = quantity;
        this.minimumStockLevel = minimumStockLevel;
    }

    // Methods ─────────────────────────────────
    /**
     * Checks whether the stock is below the minimum level.
     * @return true if low stock alert should be raised */

    public boolean isLowStock() {
        return quantity <= minimumStockLevel;
    }

    /**
     * Restocks the inventory by adding more units*/

    public void restock(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Restock amount must be positive.");
        quantity += amount;
        System.out.println("Restocked " + amount + " units. New quantity: " + quantity);
    }

    /** Reduces the inventory when a product is sold.
     */
    public void reduceStock(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive.");
        if (amount > quantity)
            throw new IllegalStateException("Not enough stock. Available: " + quantity);
        quantity -= amount;
    }

    //  Getters & Setters ────────────────────────────────────────

    public String getInventoryId() { return inventoryId; }


    public String getProductId() { return productId; }


    public int getQuantity() { return quantity; }


    public void setQuantity(int quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Quantity cannot be negative.");
        this.quantity = quantity;
    }


    public int getMinimumStockLevel() { return minimumStockLevel; }


    public void setMinimumStockLevel(int minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    // toString
    @Override
    public String toString() {
        return "Inventory{productId='" + productId + "', quantity=" + quantity +
                ", lowStock=" + isLowStock() + "}";
    }
}
