package techecommerce1;

/**
 * Abstract base class representing a system user.
 * Contains shared attributes for all user types
 */
public abstract class User {

    // Attributes ──────────────────────────────────────────

    private String userId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    //Constructor ──────────────────────────────────────────────

    public User(String userId, String name, String email, String password, String phoneNumber)
    {
        this.userId = userId;
        this.name   = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    // Abstract Method ─────────────────────────────────────────
    /**
     * Returns the role of the user ("Customer", "Admin")
     */
    public abstract String getRole();

    // Methods ───────────────────────────────────────
    /**
     * Validates the user's login credentials
     */
    public boolean login(String email, String password) {
        if (email == null || password == null)
            throw new IllegalArgumentException("Email and password cannot be null.");
        return this.email.equals(email) && this.password.equals(password);
    }

    /**
     * Logs the user out (placeholder for session logic)
     */
    public void logout() {
        System.out.println(name + " has logged out.");
    }

    // Getters & Setters ────────────────────────────────────────

    public String getUserId() { return userId; }


    public void setUserId(String userId) { this.userId = userId; }


    public String getName() { return name; }


    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;
    }


    public String getEmail() { return email; }


    public void setEmail(String email) { this.email = email; }


    public String getPassword() { return password; }


    public void setPassword(String password) { this.password = password; }


    public String getPhoneNumber() { return phoneNumber; }


    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // toString ──────────────────────────────────
    @Override
    public String toString() {
        return "User{id='" + userId + "', name='" + name +
                "', email='" + email + "', role='" + getRole() + "'}";
    }
}
