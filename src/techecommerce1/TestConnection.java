package techecommerce1;


    import java.sql.*;

    public class TestConnection {
        public static void main(String[] args) {
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/tech_ecommerce",
                        "root",
                        "your_password_here"  // ← كلمة مرورك
                );
                System.out.println("✅ Connected successfully!");
                conn.close();
            } catch (SQLException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

