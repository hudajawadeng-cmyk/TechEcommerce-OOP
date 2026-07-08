package techecommerce1.presentation;

import techecommerce1.dal.DatabaseManager;
import techecommerce1.domain.*;

import javax.swing.*;
import java.awt.*;

/**
 * Login screen – dark, modern tech aesthetic.
 */
public class LoginFrame extends JFrame {

    // ── Palette (Light Theme) ────────────────────────────────────
    static final Color BG_DARK    = new Color(247, 248, 250);   // page background (light)
    static final Color BG_CARD    = new Color(255, 255, 255);   // card background (white)
    static final Color ACCENT     = new Color(25, 103, 210);    // vivid blue accent
    static final Color ACCENT2    = new Color(111, 66, 193);    // vivid purple accent
    static final Color TEXT_MAIN  = new Color(18, 22, 34);      // near-black, high contrast
    static final Color TEXT_DIM   = new Color(90, 99, 122);     // clear medium gray-blue
    static final Color BORDER_C   = new Color(206, 212, 224);   // light gray border
    static final Color ERROR_C    = new Color(211, 47, 47);     // clear red
    static final Color SUCCESS_C  = new Color(0, 150, 90);      // clear green

    // ── Components ───────────────────────────────────────────────
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JLabel         statusLabel;
    private DatabaseManager db;

    public LoginFrame() {
        db = new DatabaseManager();
        setTitle("TechCommerce — تسجيل الدخول");
        setSize(640, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        //setUndecorated(true);
        setBackground(BG_DARK);
        initComponents();
        this.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        this.getLayeredPane().applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
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
                        new Color(225, 232, 248));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Accent glow top-left
                RadialGradientPaint rg = new RadialGradientPaint(60, 60, 120,
                        new float[]{0f, 1f},
                        new Color[]{new Color(210, 224, 250, 180), new Color(0, 0, 0, 0)});
                g2.setPaint(rg);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        root.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        root.setBorder(BorderFactory.createEmptyBorder(30, 40, 25, 40));

        // ── Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel logo = new JLabel("⬡ TechCommerce");
        logo.setFont(new Font("Monospaced", Font.BOLD, 24));
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
        JLabel signIn = new JLabel("تسجيل الدخول");
        signIn.setFont(new Font("Arial", Font.BOLD, 22));
        signIn.setForeground(TEXT_MAIN);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        card.add(signIn, gc);

        gc.gridwidth = 1; gc.insets = new Insets(6, 0, 4, 8);

        // Email
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.3;
        card.add(makeLabel("البريد الالكتروني"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        emailField = makeDarkField(18);
        card.add(emailField, gc);

        // Password
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.3;
        card.add(makeLabel("كلمة المرور"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        passwordField = new JPasswordField(18);
        styleField(passwordField);
        card.add(passwordField, gc);

        // Role
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.3;
        card.add(makeLabel("الصلاحية"), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        roleBox = new JComboBox<>(new String[]{"عميل", "مسؤول"});
        roleBox.setBackground(new Color(255,255,255));
        roleBox.setForeground(TEXT_DIM);
        roleBox.setFont(new Font("Arial", Font.PLAIN, 15));
        roleBox.setBorder(BorderFactory.createLineBorder(BORDER_C));
        card.add(roleBox, gc);

        // Buttons row
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2; gc.insets = new Insets(14, 0, 4, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton loginBtn = makeAccentButton("تسجيل الدخول", ACCENT, BG_DARK);
        JButton clearBtn = makeGhostButton("مسح");
        JButton registerBtn = makeGhostButton("حساب جديد");
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
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(ERROR_C);
        card.add(statusLabel, gc);

        root.add(card, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2026 TechCommerce Inc.", SwingConstants.CENTER);
        footer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        footer.setForeground(TEXT_DIM);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        root.add(footer, BorderLayout.SOUTH);

        add(root);

        passwordField.addActionListener(e -> handleLogin());
    }

    // ── Helpers ──────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text + ":");
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        l.setForeground(TEXT_DIM);
        return l;
    }

    private JTextField makeDarkField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(255,255,255));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Arial", Font.PLAIN, 15));
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
        b.setFont(new Font("Arial", Font.BOLD, 15));
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
                Color border = BORDER_C;
                g2.setColor(getModel().isRollover() ? new Color(225, 230, 240) : new Color(242, 244, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(TEXT_MAIN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setFont(new Font("Arial", Font.PLAIN, 15));
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
            statusLabel.setText("⚠  البريد الالكتروني وكلمة المرور مطلوبان.");
            return;
        }
        if (!email.contains("@")) {
            statusLabel.setForeground(ERROR_C);
            statusLabel.setText("⚠  يرجى ادخال بريد الكتروني صحيح.");
            return;
        }

        // ── Authenticate against MySQL ──────────────────────────
        DatabaseManager db = DatabaseManager.getInstance();

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

        //String[] user = new String[]{"U0001", "Huda Emrage", "عميل"};
        String[] user = db.authenticateUser(email, password);
        if (user == null) {
            statusLabel.setForeground(ERROR_C);
            statusLabel.setText("✖  البريد الالكتروني أو كلمة المرور غير صحيحة.");
            return;
        }

        // user[0]=user_id  user[1]=name  user[2]=role
        statusLabel.setForeground(SUCCESS_C);
        statusLabel.setText("✔  أهلاً بك مجدداً " + user[1] + "!");
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

        // 1. إنشاء العميل والمنتج وسلة التسوق
        Customer customer = new Customer("C01", "أحمد", "ahmed@email.com", "pass", "1234", "طرابلس، ليبيا");
        Product phone = new Product("P10", "iPhone", 1000.0, 5);

        customer.addToCart(phone, 1);

        // 2. تطبيق Strategy: اختيار وسيلة الدفع أثناء التشغيل وتمريرها
        PaymentStrategy cardPayment = new CreditCardPayment("1234567812345678", "Ahmed", "12/29", "123");
        Order order = customer.placeOrder("ORD-99", "2026-07-08", cardPayment);

        // 3. تطبيق Observer: إنشاء الشحن وربط العميل به كمراقب
        ShippingInfo shipping = new ShippingInfo("TRK-1002", order.getOrderId(), customer.getShippingAddress(), "DHL", "2026-07-12");
        shipping.attach(customer); // العميل الآن يراقب حالة هذا الشحن

        System.out.println("\n--- محاكاة لتحديث حالة الشحن من قِبل النظام ---");
        // بمجرد تحديث الحالة، سيتلقى العميل إشعاراً تلقائياً في الكونسول دون استدعاء يدوي للعميل!
        shipping.updateStatus("SHIPPED");
        shipping.updateStatus("DELIVERED");
    }
}