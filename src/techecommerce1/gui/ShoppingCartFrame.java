package techecommerce1.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Screen displaying the customer's shopping cart.
 * Shows selected products, quantities, and total price.*/
public class ShoppingCartFrame extends JFrame {

    // ── Components ───────────────────────────────────────────────
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;

    // ── Constructor ──────────────────────────────────────────────
    /**
     * Constructs the Shopping Cart screen.
     */
    public ShoppingCartFrame() {
        setTitle("Shopping Cart 🛒");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        loadSampleCart();
    }

    // ── UI Setup ─────────────────────────────────────────────────
    /**
     * Initializes all UI components for the cart view.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 248, 255));

        // Title
        JLabel title = new JLabel("Your Shopping Cart", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 160));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"Product ID", "Product Name", "Unit Price ($)", "Qty", "Subtotal ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(28);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(new Color(255, 140, 0));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setSelectionBackground(new Color(255, 220, 180));

        JScrollPane scrollPane = new JScrollPane(cartTable);

        // Total label
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(new Color(30, 80, 160));

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setBackground(new Color(245, 248, 255));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(new Color(245, 248, 255));

        JButton removeBtn = new JButton("🗑 Remove Item");
        removeBtn.setBackground(new Color(200, 50, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFocusPainted(false);
        removeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        removeBtn.addActionListener(e -> handleRemove());

        JButton checkoutBtn = new JButton("✅ Checkout");
        checkoutBtn.setBackground(new Color(30, 160, 80));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        checkoutBtn.addActionListener(e -> handleCheckout());

        JButton closeBtn = new JButton("Close");
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(removeBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(closeBtn);

        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // ── Data ─────────────────────────────────────────────────────
    /**
     * Loads sample cart items for demonstration.
     */
    private void loadSampleCart() {
        Object[][] items = {
                {"P001", "Dell XPS 15",      1299.99, 1, 1299.99},
                {"P004", "Samsung 1TB SSD",   129.99, 2,  259.98},
        };
        for (Object[] row : items) tableModel.addRow(row);
        updateTotal();
    }

    /**
     * Recalculates and displays the cart total.
     */
    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 4);
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    // ── Handlers ─────────────────────────────────────────────────
    /**
     * Removes the selected item from the cart.
     */
    private void handleRemove() {
        int selected = cartTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String productName = tableModel.getValueAt(selected, 1).toString();
        tableModel.removeRow(selected);
        updateTotal();
        JOptionPane.showMessageDialog(this,
                productName + " removed from cart.",
                "Removed", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles the checkout process.
     */
    private void handleCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!",
                    "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Proceed to checkout?\n" + totalLabel.getText(),
                "Confirm Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            updateTotal();
            JOptionPane.showMessageDialog(this,
                    "Order placed successfully! 🎉\nYou will receive a confirmation email.",
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}


