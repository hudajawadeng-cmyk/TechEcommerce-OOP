package techecommerce1;



/**
 * Represents a customer review for a product
 * Allows customers to provide feedback and star ratings.
 *
 */
public class Review {

    // Attributes ──────────────────────────────────────────────
    private String reviewId;
    private String customerId;
    private String productId;
    private int rating;      // 1 to 5 stars
    private String comment;
    private String reviewDate;

    // Constructor ──────────────────────────────────────

    public Review(String reviewId, String customerId, String productId, int rating, String comment, String reviewDate)
    {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");

        this.reviewId   = reviewId;
        this.customerId = customerId;
        this.productId  = productId;
        this.rating     = rating;
        this.comment    = comment;
        this.reviewDate = reviewDate;
    }

    // Methods ──────────────────────────────────────────────────
    /**
     * Returns a formatted display of the review.
     */
    public String displayReview() {
        return "⭐".repeat(rating) + " | " + comment + " (" + reviewDate + ")";
    }

    // Getters & Setters ────────────────────────────

    public String getReviewId() { return reviewId; }


    public String getCustomerId() { return customerId; }


    public String getProductId() { return productId; }


    public int getRating() { return rating; }


    public void setRating(int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        this.rating = rating;
    }


    public String getComment() { return comment; }


    public void setComment(String comment) { this.comment = comment; }


    public String getReviewDate() { return reviewDate; }

    //toString ─────────────────────────────────────────────────
    @Override
    public String toString() {
        return "Review{product='" + productId + "', rating=" + rating +
                ", comment='" + comment + "'}";
    }
}
