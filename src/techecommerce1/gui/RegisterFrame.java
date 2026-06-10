package techecommerce1.gui;

import techecommerce1.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Register Frame — Create new Account
 */
public class RegisterFrame extends JFrame {

    private JTextField    nameField, emailField, phoneField;
    private JPasswordField passwordField, confirmField;
    private JComboBox<String> roleBox, countryBox;
    private JLabel        statusLabel;
    private DatabaseManager db;

    public RegisterFrame() {
        this.db = DatabaseManager.getInstance();
        setTitle("TechCommerce — Create Account");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setBackground(BG_DARK);
        initComponents();
    }

    private void initComponents() {
        // ── Root panel
        JPanel root = new JPanel(new BorderLayout(0, 14)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(15, 18, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent glow top-right
                RadialGradientPaint rg = new RadialGradientPaint(getWidth() - 60, 60, 120,
                        new float[]{0f, 1f},
                        new Color[]{new Color(100, 60, 255, 30), new Color(0, 0, 0, 0)});
                g2.setPaint(rg);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(24, 36, 20, 36));

        // ── Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel logo = new JLabel("⬡ TechCommerce");
        logo.setFont(new Font("Monospaced", Font.BOLD, 20));
        logo.setForeground(ACCENT);
        JLabel sub = new JLabel("Create a new account");
        sub.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sub.setForeground(TEXT_DIM);
        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        logoPanel.add(sub);
        header.add(logoPanel, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // ── Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 22, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.insets  = new Insets(6, 0, 6, 8);
        gc.gridwidth = 1;

        // ── Section: Account Info
        addSectionLabel(card, gc, 0, "Account Information");

        nameField    = darkField(18);
        emailField   = darkField(18);
        passwordField  = darkPasswordField(18);
        confirmField   = darkPasswordField(18);

        addRow(card, gc, 1, "Full Name",         nameField);
        addRow(card, gc, 2, "Email",              emailField);
        addRow(card, gc, 3, "Password",           passwordField);
        addRow(card, gc, 4, "Confirm Password",   confirmField);

        // ── Separator
        addSeparator(card, gc, 5);

        // ── Section: Personal Info
        addSectionLabel(card, gc, 6, "Personal Details  (optional)");

        phoneField = darkField(18);
        countryBox = new JComboBox<>(new String[]{"Libya","Saudi Arabia","UAE","Egypt","United States","Other"});
        styleCombo(countryBox);
        roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        styleCombo(roleBox);

        addRow(card, gc, 7,  "Phone",    phoneField);
        addRow(card, gc, 8,  "Country",  countryBox);
        addRow(card, gc, 9,  "Role",     roleBox);

        // ── Status label
        gc.gridx = 0; gc.gridy = 10; gc.gridwidth = 2;
        gc.insets = new Insets(6, 0, 0, 0);
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(ERROR_C);
        card.add(statusLabel, gc);

        root.add(card, BorderLayout.CENTER);

        // ── Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);

        JButton registerBtn = makeAccentButton("✔  Register", ACCENT2, BG_DARK);
        registerBtn.setPreferredSize(new Dimension(140, 36));

        JButton clearBtn  = makeGhostButton("Clear");
        JButton cancelBtn = makeGhostButton("Cancel");

        registerBtn.addActionListener(e -> handleRegister());
        clearBtn   .addActionListener(e -> clearFields());
        cancelBtn  .addActionListener(e -> dispose());

        btnRow.add(registerBtn);
        btnRow.add(clearBtn);
        btnRow.add(cancelBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    // ── Registration Logic ────────────────────────────────────────
    private void handleRegister() {
        // ── Read fields
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String phone    = phoneField.getText().trim();
        String country  = countryBox.getSelectedItem().toString();
        String role     = roleBox.getSelectedItem().toString();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();

        // ── Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            setStatus("⚠  Name, Email, and Password are required.", ERROR_C);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            setStatus("⚠  Please enter a valid email address.", ERROR_C);
            return;
        }
        if (password.length() < 6) {
            setStatus("⚠  Password must be at least 6 characters.", ERROR_C);
            return;
        }
        if (!password.equals(confirm)) {
            setStatus("✖  Passwords do not match.", ERROR_C);
            return;
        }

        // ── Generate unique user ID
        String userId = "U" + String.format("%04d", (int)(Math.random() * 9000 + 1000));

        // ── Save to DB or demo mode
        if (db.isConnected()) {
            boolean success = db.registerUser(userId, name, email, password, phone, country, role);
            if (!success) {
                setStatus("✖  Email already exists or DB error.", ERROR_C);
                return;
            }
        }

        // ── Success
        setStatus("✔  Account created successfully!", SUCCESS_C);
        JOptionPane.showMessageDialog(this,
                "🎉 Welcome, " + name + "!\n\nYour account has been created.\nYou can now sign in.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    // ── UI Helpers ────────────────────────────────────────────────
    private void addSectionLabel(JPanel card, GridBagConstraints gc, int row, String text) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.insets = new Insets(10, 0, 4, 0);
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(ACCENT);
        card.add(lbl, gc);
        gc.gridwidth = 1;
        gc.insets = new Insets(6, 0, 6, 8);
    }

    private void addSeparator(JPanel card, GridBagConstraints gc, int row) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.insets = new Insets(8, 0, 4, 0);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        card.add(sep, gc);
        gc.gridwidth = 1;
        gc.insets = new Insets(6, 0, 6, 8);
    }

    private void addRow(JPanel card, GridBagConstraints gc, int row, String label, Component field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.35;
        JLabel l = new JLabel(label + ":");
        l.setFont(new Font("Monospaced", Font.PLAIN, 11));
        l.setForeground(TEXT_DIM);
        card.add(l, gc);
        gc.gridx = 1; gc.weightx = 0.65;
        card.add(field, gc);
    }

    private JTextField darkField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(new Color(25, 32, 55));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private JPasswordField darkPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setBackground(new Color(25, 32, 55));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return f;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(new Color(25, 32, 55));
        box.setForeground(TEXT_DIM);
        box.setFont(new Font("Monospaced", Font.PLAIN, 12));
        box.setBorder(BorderFactory.createLineBorder(BORDER_C));
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        countryBox.setSelectedIndex(0);
        roleBox.setSelectedIndex(0);
        statusLabel.setText(" ");
        nameField.requestFocus();
    }
}