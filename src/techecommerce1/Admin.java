package techecommerce1;

/**
 * Represents an administrator user of the e-commerce platform.
 * Inherits from User and manages products and inventory.
 */
public class Admin extends User {

    // Attributes ──────────────────────────────────────────────
    private String adminLevel; //  "SUPER", "REGULAR"
    private String department;

    //  Constructor

    public Admin(String userId, String name, String email, String password,
                 String phoneNumber, String adminLevel, String department) {
        super(userId, name, email, password, phoneNumber);
        this.adminLevel = adminLevel;
        this.department = department;
    }

    //  Overridden Method ────────────────────────────────────────
    /**
     * Returns the role of this user.
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    // Methods ──────────────────────────
    /**
     * Adds a new product to the catalog */
    public void addProduct(Product product) {
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null.");
        System.out.println("Admin " + getName() + " added product: " + product.getName());
    }

    /**
     * Removes a product from the catalog by ID.*/
    public void removeProduct(String productId) {
        if (productId == null || productId.isBlank())
            throw new IllegalArgumentException("Product ID cannot be empty.");
        System.out.println("Admin " + getName() + " removed product ID: " + productId);
    }

    /**
     * Updates the stock quantity of a product in the inventory */
    public void updateInventory(Inventory inventory, int quantity) {
        if (inventory == null)
            throw new IllegalArgumentException("Inventory cannot be null.");
        inventory.setQuantity(quantity);
        System.out.println("Inventory updated to: " + quantity);
    }

    // Getters & Setters

    public String getAdminLevel() { return adminLevel; }


    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }


    public String getDepartment() { return department; }


    public void setDepartment(String department) { this.department = department; }

    //  toString ──────────────────────────────────────────

    @Override
    public String toString() {
        return "Admin{id='" + getUserId() + "', name='" + getName() +
                "', level='" + adminLevel + "', dept='" + department + "'}";
    }
}
