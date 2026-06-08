package techecommerce1.gui;

import javax.swing.*;
import java.awt.*;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Add New Product form — dark theme.
 */
public class AddProductFrame extends JFrame {

    private JTextField idField, nameField, brandField, priceField, stockField, specsField;
    private JComboBox<String> categoryBox;

    public AddProductFrame() {
        setTitle("TechCommerce — Add New Product");
        setSize(480, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setBackground(BG_DARK);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(10, 14));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(22, 28, 18, 28));

        JLabel title = new JLabel("➕  Add New Product");
        title.setFont(new Font("Monospaced", Font.BOLD, 17));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        root.add(title, BorderLayout.NORTH);

        // Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,22,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20,22,20,22));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7,0,7,10);

        String[] labels = {"Product ID","Name","Brand","Price ($)","Category","Stock","Specifications"};
        idField       = darkField();
        nameField     = darkField();
        brandField    = darkField();
        priceField    = darkField();
        categoryBox   = new JComboBox<>(new String[]{"Laptops","Networking","Storage","Sensors","Servers","Other"});
        categoryBox.setBackground(new Color(25,32,55));
        categoryBox.setForeground(TEXT_MAIN);
        categoryBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        stockField    = darkField();
        specsField    = darkField();
        Component[] fields = {idField, nameField, brandField, priceField, categoryBox, stockField, specsField};

        for (int i = 0; i < labels.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.35;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Monospaced", Font.PLAIN, 11));
            l.setForeground(TEXT_DIM);
            card.add(l, gc);
            gc.gridx=1; gc.weightx=0.65;
            card.add(fields[i], gc);
        }
        root.add(card, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 6));
        btnRow.setOpaque(false);

        JButton saveBtn  = makeAccentButton("💾  Save",  ACCENT, BG_DARK);
        JButton clearBtn = makeGhostButton("Clear");
        JButton cancelBtn = makeGhostButton("Cancel");
        saveBtn.addActionListener(e -> handleSave());
        clearBtn.addActionListener(e -> clearFields());
        cancelBtn.addActionListener(e -> dispose());

        btnRow.add(saveBtn);
        btnRow.add(clearBtn);
        btnRow.add(cancelBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    private JTextField darkField() {
        JTextField f = new JTextField(16);
        f.setBackground(new Color(25,32,55));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5,8,5,8)));
        return f;
    }

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
            if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
            if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative.");

            JOptionPane.showMessageDialog(this,
                    "✔ Product '" + name + "' added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Price and Stock must be valid numbers.","Error",JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Validation Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); brandField.setText("");
        priceField.setText(""); stockField.setText(""); specsField.setText("");
        categoryBox.setSelectedIndex(0);
        idField.requestFocus();
    }
}