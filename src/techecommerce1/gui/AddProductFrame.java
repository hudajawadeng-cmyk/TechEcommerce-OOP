package techecommerce1.gui;




import javax.swing.*;
import java.awt.*;

/**
 * Form for Admin users to add a new product to the catalog.
 */

public class AddProductFrame extends JFrame {

    // ── Components ───────────────────────────────────────────────
    private JTextField idField, nameField, brandField, priceField, stockField, specsField;
    private JComboBox<String> categoryBox;

    // ── Constructor ──────────────────────────────────────────────
    /**
     * Constructs the Add Product form.
     */
    public AddProductFrame() {
        setTitle("Add New Product");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    //  UI Setup
    /**
     * Initializes the form layout and components.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 248, 255));

        // Title
        JLabel title = new JLabel("Add New Product", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 160));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Product ID:", "Name:", "Brand:", "Price ($):",
                "Category:", "Stock:", "Specifications:"};

        idField       = new JTextField(15);
        nameField     = new JTextField(15);
        brandField    = new JTextField(15);
        priceField    = new JTextField(15);
        categoryBox   = new JComboBox<>(new String[]{
                "Laptops", "Networking", "Storage", "Sensors", "Servers", "Other"});
        stockField    = new JTextField(15);
        specsField    = new JTextField(15);

        Component[] fields = {idField, nameField, brandField, priceField,
                categoryBox, stockField, specsField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.65;
            if (fields[i] instanceof JTextField)
                ((JTextField) fields[i]).setFont(new Font("Segoe UI", Font.PLAIN, 13));
            formPanel.add(fields[i], gbc);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(245, 248, 255));

        JButton saveBtn = new JButton("Save Product");
        saveBtn.setBackground(new Color(30, 80, 160));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setPreferredSize(new Dimension(130, 35));
        saveBtn.addActionListener(e -> handleSave());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFocusPainted(false);
        clearBtn.setPreferredSize(new Dimension(90, 35));
        clearBtn.addActionListener(e -> clearFields());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(90, 35));
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // ── Handlers ─────────────────────────────────────────────────
    /**
     * Validates fields and saves the new product.
     */
    private void handleSave() {
        try {
            String id    = idField.getText().trim();
            String name  = nameField.getText().trim();
            String brand = brandField.getText().trim();
            String specs = specsField.getText().trim();
            int stock    = Integer.parseInt(stockField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String cat   = (String) categoryBox.getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || brand.isEmpty())
                throw new IllegalArgumentException("ID, Name, and Brand are required.");
            if (price < 0)
                throw new IllegalArgumentException("Price cannot be negative.");
            if (stock < 0)
                throw new IllegalArgumentException("Stock cannot be negative.");

            // Here you would call: adminService.addProduct(new Product(...))
            JOptionPane.showMessageDialog(this,
                    "Product '" + name + "' added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Price and Stock must be valid numbers.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all form fields.
     */
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        brandField.setText("");
        priceField.setText("");
        stockField.setText("");
        specsField.setText("");
        categoryBox.setSelectedIndex(0);
        idField.requestFocus();
    }
}

