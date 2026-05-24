package techecommerce1.gui;


import javax.swing.*;
import java.awt.*;

/**
 * Login screen for the Tech E-commerce Platform.
 * Allows users to enter their email and password to access the system
 */
public class LoginFrame extends JFrame {

    // ── Components ───────────────────────────────────────────────
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton clearButton;
    private JLabel statusLabel;

    // ── Constructor ──────────────────────────────────────────────
    /**
     * Constructs and displays the Login window.
     */
    public LoginFrame() {
        setTitle("Tech E-commerce Platform — Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setResizable(false);

        initComponents();
    }

    // ── UI Setup ─────────────────────────────────────────────────
    /**
     * Initializes and arranges all UI components.
     */
    private void initComponents() {
        // ── Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(245, 248, 255));

        // ── Title
        JLabel titleLabel = new JLabel("Tech E-Commerce", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 80, 160));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ── Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email label
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Email:"), gbc);

        // Email field
        gbc.gridx = 1; gbc.weightx = 0.7;
        emailField = new JTextField(18);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(emailField, gbc);

        // Password label
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Password:"), gbc);

        // Password field
        gbc.gridx = 1; gbc.weightx = 0.7;
        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(passwordField, gbc);

        // Role selector
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Login as:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        roleBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(roleBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ── Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(245, 248, 255));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setBackground(new Color(30, 80, 160));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clearButton.setPreferredSize(new Dimension(100, 35));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        buttonPanel.add(loginButton);
        buttonPanel.add(clearButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(245, 248, 255));
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ── Action Listeners
        loginButton.addActionListener(e -> handleLogin(roleBox));
        clearButton.addActionListener(e -> clearFields());

        // Press Enter to login
        passwordField.addActionListener(e -> handleLogin(roleBox));
    }

    // ── Event Handlers ───────────────────────────────────────────
    /**
     * Handles login button click.
     * Validates input and opens the appropriate dashboard.
     *
     * @param roleBox the role combo box
     */
    private void handleLogin(JComboBox<String> roleBox) {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role     = (String) roleBox.getSelectedItem();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        if (!email.contains("@")) {
            statusLabel.setText("Please enter a valid email address.");
            return;
        }

        // Simulate successful login (replace with DB check later)
        statusLabel.setForeground(new Color(0, 128, 0));
        statusLabel.setText("Login successful! Welcome, " + role + ".");

        // Open main dashboard
        SwingUtilities.invokeLater(() -> {
            new MainDashboard(email, role).setVisible(true);
            dispose(); // close login window
        });
    }

    /**
     * Clears the email and password fields.
     */
    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText("");
        statusLabel.setForeground(Color.RED);
        emailField.requestFocus();
    }

    // ── Main ─────────────────────────────────────────────────────
    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}


