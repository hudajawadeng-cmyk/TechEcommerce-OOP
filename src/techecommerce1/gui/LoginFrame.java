package techecommerce1.gui;

import techecommerce1.db.DatabaseManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Login screen – dark, modern tech aesthetic.
 */
public class LoginFrame extends JFrame {

    // ── Palette ──────────────────────────────────────────────────
    static final Color BG_DARK    = new Color(10, 12, 20);
    static final Color BG_CARD    = new Color(18, 22, 38);
    static final Color ACCENT     = new Color(0, 200, 255);
    static final Color ACCENT2    = new Color(100, 60, 255);
    static final Color TEXT_MAIN  = new Color(220, 230, 255);
    static final Color TEXT_DIM   = new Color(100, 120, 160);
    static final Color BORDER_C   = new Color(40, 55, 90);
    static final Color ERROR_C    = new Color(255, 70, 100);
    static final Color SUCCESS_C  = new Color(0, 220, 140);

    // ── Components ───────────────────────────────────────────────
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JLabel         statusLabel;
    private DatabaseManager db;

    public LoginFrame() {
        db = new DatabaseManager();
        setTitle("TechCommerce — Login");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(BG_DARK);
        initComponents();
    }

    private void initComponents() {
        // Root panel with custom painting
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(),
                        new Color(15, 18, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent glow top-left
                RadialGradientPaint rg = new RadialGradientPaint(60, 60, 120,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 200, 255, 30), new Color(0, 0, 0, 0)});
                g2.setPaint(rg);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 25, 40));

        // ── Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel logo = new JLabel("⬡ TechCommerce");
        logo.setFont(new Font("Monospaced", Font.BOLD, 22));
        logo.setForeground(ACCENT);



        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        logoPanel.setOpaque(false);
        logoPanel.add(logo);
        header.add(logoPanel, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // ── Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(7, 0, 7, 0);

        // Sign-in label
        JLabel signIn = new JLabel("Sign In");
        signIn.setFont(new Font("Monospaced", Font.BOLD, 17));
        signIn.setForeground(TEXT_MAIN);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        card.add(signIn, gc);

        gc.gridwidth = 1; gc.insets = new Insets(6, 0, 4, 8);

        // Email
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.3;
        card.add(makeLabel("Email"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        emailField = makeDarkField(18);
        card.add(emailField, gc);

        // Password
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.3;
        card.add(makeLabel("Password"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        passwordField = new JPasswordField(18);
        styleField(passwordField);
        card.add(passwordField, gc);

        // Role
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.3;
        card.add(makeLabel("Role"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        roleBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        roleBox.setBackground(new Color(25, 32, 55));
        roleBox.setForeground(TEXT_DIM);
        roleBox.setFont(new Font("Monospaced", Font.PLAIN, 13));
        roleBox.setBorder(BorderFactory.createLineBorder(BORDER_C));
        card.add(roleBox, gc);

        // Buttons row
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.insets = new Insets(14, 0, 4, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton loginBtn = makeAccentButton("→  Sign In", ACCENT, BG_DARK);
        JButton clearBtn = makeGhostButton("Clear");
        JButton registerBtn = makeGhostButton("New Account");
        loginBtn.addActionListener(e -> handleLogin());
        clearBtn.addActionListener(e -> clearFields());
        registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
        btnRow.add(loginBtn);
        btnRow.add(clearBtn);
        btnRow.add(registerBtn);
        card.add(btnRow, gc);

        // Status
        gc.gridy = 5; gc.insets = new Insets(4, 0, 0, 0);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLabel.setForeground(ERROR_C);
        card.add(statusLabel, gc);

        root.add(card, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2026 TechCommerce Inc.", SwingConstants.CENTER);
        footer.setFont(new Font("Monospaced", Font.PLAIN, 10));
        footer.setForeground(TEXT_DIM);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        root.add(footer, BorderLayout.SOUTH);

        add(root);

        passwordField.addActionListener(e -> handleLogin());
    }

    // ── Helpers ──────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text + ":");
        l.setFont(new Font("Monospaced", Font.PLAIN, 12));
        l.setForeground(TEXT_DIM);
        return l;
    }

    private JTextField makeDarkField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(25, 32, 55));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Monospaced", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    static JButton makeAccentButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(fg);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(new Font("Monospaced", Font.BOLD, 13));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(130, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JButton makeGhostButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = new Color(60, 80, 130);
                g2.setColor(getModel().isRollover() ? new Color(30, 40, 70) : new Color(20, 28, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(new Color(180, 200, 240));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(new Font("Monospaced", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(90, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Handlers ─────────────────────────────────────────────────
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(ERROR_C);
            statusLabel.setText("⚠  Email and password are required.");
            return;
        }
        if (!email.contains("@")) {
            statusLabel.setForeground(ERROR_C);
            statusLabel.setText("⚠  Enter a valid email address.");
            return;
        }

        // ── Authenticate against MySQL ──────────────────────────
        techecommerce1.db.DatabaseManager db = techecommerce1.db.DatabaseManager.getInstance();

        if (!db.isConnected()) {
            // DB offline → allow demo login with any credentials
            //statusLabel.setForeground(new Color(255,200,50));
            //statusLabel.setText("⚠  DB offline — demo mode.");
            String role = (String) roleBox.getSelectedItem();
            SwingUtilities.invokeLater(() -> {
                new MainDashboard(null, email, role).setVisible(true);
                dispose();
            });
            return;
        }

        //String[] user = new String[]{"U0001", "Huda Emrage", "Customer"};
        String[] user = db.authenticateUser(email, password);
        if (user == null) {
            statusLabel.setForeground(ERROR_C);
            statusLabel.setText("✖  Invalid email or password.");
            return;
        }

        // user[0]=user_id  user[1]=name  user[2]=role
        statusLabel.setForeground(SUCCESS_C);
        statusLabel.setText("✔  Welcome back, " + user[1] + "!");
        String userId = user[0];
        String role   = user[2];
        SwingUtilities.invokeLater(() -> {
            new MainDashboard(userId,email, role).setVisible(true);
            dispose();
        });
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        statusLabel.setText("");
        emailField.requestFocus();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}