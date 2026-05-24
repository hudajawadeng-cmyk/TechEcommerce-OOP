package techecommerce1.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Screen that displays all available products in a table.
 * Admin users see an additional Delete button */
public class ProductListFrame extends JFrame {

    // ── Components ───────────────────────────────────────────────
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private String userRole;

    // ── Constructor ──────────────────────────────────────────────
    /**
     * Constructs the ProductListFrame.
     *
     * @param userRole "Customer" or "Admin"
     */
    public ProductListFrame(String userRole) {
        this.userRole = userRole;
        setTitle("Browse Products");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadSampleData();
    }

    // ── UI Setup ─────────────────────────────────────────────────
    /**
     * Initializes all UI components for the product list.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title
        JLabel title = new JLabel("Available Products", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 160));
        mainPanel.add(title, BorderLayout.NORTH);

        // ── Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(30, 80, 160));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // ── Table
        String[] columns = {"ID", "Name", "Brand", "Price ($)", "Category", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(new Color(30, 80, 160));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionBackground(new Color(200, 220, 255));

        JScrollPane scrollPane = new JScrollPane(productTable);

        // ── Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addToCartBtn = new JButton("🛒 Add to Cart");
        addToCartBtn.setBackground(new Color(255, 140, 0));
        addToCartBtn.setForeground(Color.WHITE);
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addToCartBtn.addActionListener(e -> handleAddToCart());

        JButton closeBtn = new JButton("Close");
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(addToCartBtn);

        if (userRole.equals("Admin")) {
            JButton deleteBtn = new JButton("🗑 Delete Product");
            deleteBtn.setBackground(new Color(200, 50, 50));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            deleteBtn.addActionListener(e -> handleDelete());
            buttonPanel.add(deleteBtn);
        }

        buttonPanel.add(closeBtn);

        // ── Assemble
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Search action
        searchBtn.addActionListener(e -> filterTable(searchField.getText().trim()));
    }

    // ── Data ─────────────────────────────────────────────────────
    /**
     * Loads sample product data into the table for demonstration.
     */
    private void loadSampleData() {
        Object[][] data = {
                {"P001", "Dell XPS 15",       "Dell",    1299.99, "Laptops",    15},
                {"P002", "MacBook Pro 14",    "Apple",   1999.00, "Laptops",     8},
                {"P003", "Cisco Switch 24P",  "Cisco",    450.00, "Networking", 20},
                {"P004", "Samsung 1TB SSD",   "Samsung",  129.99, "Storage",    50},
                {"P005", "Arduino Mega",      "Arduino",   38.50, "Sensors",   100},
                {"P006", "HP ProBook 450",    "HP",       899.00, "Laptops",    12},
                {"P007", "Seagate 4TB HDD",   "Seagate",   89.99, "Storage",    35},
        };
        for (Object[] row : data) tableModel.addRow(row);
    }

    // ── Handlers ─────────────────────────────────────────────────
    /**
     * Filters the product table by name or brand.
     *
     * @param keyword search keyword
     */
    private void filterTable(String keyword) {
        // Re-load all data then filter
        tableModel.setRowCount(0);
        loadSampleData();
        if (keyword.isEmpty()) return;

        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            String name  = tableModel.getValueAt(i, 1).toString().toLowerCase();
            String brand = tableModel.getValueAt(i, 2).toString().toLowerCase();
            if (!name.contains(keyword.toLowerCase()) && !brand.contains(keyword.toLowerCase()))
                tableModel.removeRow(i);
        }
    }

    /**
     * Handles the "Add to Cart" button for the selected row.
     */
    private void handleAddToCart() {
        int selected = productTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String productName = tableModel.getValueAt(selected, 1).toString();
        JOptionPane.showMessageDialog(this,
                productName + " added to cart successfully! 🛒",
                "Added to Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles the "Delete Product" button (Admin only).
     */
    private void handleDelete() {
        int selected = productTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String productName = tableModel.getValueAt(selected, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete: " + productName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeRow(selected);
            JOptionPane.showMessageDialog(this, "Product deleted successfully.");
        }
    }
}

