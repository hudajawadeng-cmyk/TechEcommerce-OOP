package techecommerce1.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard window shown after successful login.
 * Provides navigation to all major features of the platform.*/

public class MainDashboard extends JFrame {

    // ── Fields ───────────────────────────────────────────────────
    private String userEmail;
    private String userRole;

    // ── Constructor ──────────────────────────────────────────────
    /**
     * Constructs the dashboard for the logged-in user.
     *
     * @param userEmail the logged-in user's email
     * @param userRole  "Customer" or "Admin"
     */
    public MainDashboard(String userEmail, String userRole) {
        this.userEmail = userEmail;
        this.userRole  = userRole;

        setTitle("Tech E-Commerce — Dashboard (" + userRole + ")");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    // ── UI Setup ─────────────────────────────────────────────────
    /**
     * Initializes all dashboard components and layout.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 244, 255));

        // ── Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 80, 160));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel("Welcome, " + userEmail + "  |  Role: " + userRole);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // ── Center — navigation buttons grid
        JPanel navPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        navPanel.setBackground(new Color(240, 244, 255));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        navPanel.add(createNavButton("🛍  Browse Products",
                new Color(30, 130, 200), e -> new ProductListFrame(userRole).setVisible(true)));

        navPanel.add(createNavButton("🛒  Shopping Cart",
                new Color(255, 140, 0), e -> new ShoppingCartFrame().setVisible(true)));

        navPanel.add(createNavButton("📦  My Orders",
                new Color(80, 160, 80), e -> showInfo("My Orders — Coming Soon")));

        navPanel.add(createNavButton("⭐  Product Reviews",
                new Color(160, 80, 160), e -> showInfo("Reviews — Coming Soon")));

        if (userRole.equals("Admin")) {
            navPanel.add(createNavButton("➕  Add Product",
                    new Color(30, 80, 160), e -> new AddProductFrame().setVisible(true)));
            navPanel.add(createNavButton("📊  Manage Inventory",
                    new Color(100, 100, 100), e -> showInfo("Inventory — Coming Soon")));
        } else {
            navPanel.add(createNavButton("👤  My Profile",
                    new Color(30, 80, 160), e -> showInfo("Profile — Coming Soon")));
            navPanel.add(createNavButton("🚚  Track Shipment",
                    new Color(100, 100, 100), e -> showInfo("Tracking — Coming Soon")));
        }

        mainPanel.add(navPanel, BorderLayout.CENTER);

        // ── Status bar
        JLabel statusBar = new JLabel("  Tech E-Commerce Platform  |  Sprint 2  |  2026");
        statusBar.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusBar.setForeground(Color.GRAY);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ── Helpers ──────────────────────────────────────────────────
    /**
     * Creates a styled navigation button.
     *
     * @param text     button label
     * @param color    background color
     * @param listener action to perform on click
     * @return the configured JButton
     */
    private JButton createNavButton(String text, Color color,
                                    java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        return btn;
    }

    /**
     * Shows a simple info dialog.
     *
     * @param message the message to display
     */
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }
}


