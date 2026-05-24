package techecommerce1;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product category in the e-commerce platform.
 * Organizes products into groups for easier navigation.
 *
 */
public class Category {

    // Attributes

    private String categoryId;
    private String name;
    private String description;
    private List<Product> products;

    // Constructor ──────────────────────────────────────────────

    public Category(String categoryId, String name, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
        this.products    = new ArrayList<>();
    }

    //Methods ──────────────────────────────────────────────────

    /**Adds a product to this category*/

    public void addProduct(Product product) {
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null.");
        products.add(product);
    }

    /** Removes a product from this category*/
    public void removeProduct(String productId) {
        products.removeIf(p -> p.getProductId().equals(productId));
    }

    /** Returns the number of products in this category.*/
    public int getProductCount() {
        return products.size();
    }

    // Getters & Setters ──────────────────────────────────

    public String getCategoryId() { return categoryId; }


    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }


    public String getName() { return name; }


    public void setName(String name) { this.name = name; }


    public String getDescription() { return description; }


    public void setDescription(String description) { this.description = description; }


    public List<Product> getProducts() { return products; }

    // toString ───────────────────────────────────────────

    @Override
    public String toString() {
        return "Category{id='" + categoryId + "', name='" + name +
                "', products=" + products.size() + "}";
    }
}
