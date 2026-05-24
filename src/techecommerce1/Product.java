package techecommerce1;

/**
 * Represents a technical product in the e-commerce platform.
 * Stores product details including specifications, price, and brand.
 */
public class Product {

    //Attributes ──────────────────────────────────────────────
    private String productId;
    private String name;
    private String brand;
    private double price;
    private String specifications; // "Intel i7, 16GB RAM, 512GB SSD"
    private String categoryId;
    private int stockQuantity;

    //Constructor ───────────────────────────────────────

    public Product(String productId, String name, String brand, double price,
                   String specifications, String categoryId, int stockQuantity) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative.");
        if (stockQuantity < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative.");

        this.productId      = productId;
        this.name           = name;
        this.brand          = brand;
        this.price          = price;
        this.specifications = specifications;
        this.categoryId     = categoryId;
        this.stockQuantity  = stockQuantity;
    }

    //Methods ──────────────────────────────────────────────────
    /**
     * Checks if the product is available in stock.
     * return true if stock > 0
     */
    public boolean isAvailable() {
        return stockQuantity > 0;
    }

    /**
     * Applies a discount to the product price.

     */
    public void applyDiscount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100)
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        price = price - (price * discountPercent / 100);
    }

    //Getters & Setters ─────────────────────────────────────

    public String getProductId() { return productId; }


    public void setProductId(String productId) { this.productId = productId; }


    public String getName() { return name; }


    public void setName(String name) { this.name = name; }


    public String getBrand() { return brand; }


    public void setBrand(String brand) { this.brand = brand; }


    public double getPrice() { return price; }


    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }


    public String getSpecifications() { return specifications; }


    public void setSpecifications(String specifications) { this.specifications = specifications; }


    public String getCategoryId() { return categoryId; }


    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }


    public int getStockQuantity() { return stockQuantity; }


    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        this.stockQuantity = stockQuantity;
    }

    // toString ──────────────────────────────────────────────
    @Override
    public String toString() {
        return "Product{id='" + productId + "', name='" + name + "', brand='" + brand +
                "', price=" + price + ", stock=" + stockQuantity + "}";
    }
}
