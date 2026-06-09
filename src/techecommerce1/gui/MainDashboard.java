package techecommerce1.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Main Dashboard – dark tech theme, full navigation.
 */
public class MainDashboard extends JFrame {

    private String userId;
    private String userEmail;
    private String userRole;



    public MainDashboard( String userId,String userEmail, String userRole) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole  = userRole;

        setTitle("TechCommerce ");
        setSize(720, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        initComponents();

    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle grid pattern
                g2.setColor(new Color(255, 255, 255, 6));
                for (int x = 0; x < getWidth(); x += 40)
                    g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40)
                    g2.drawLine(0, y, getWidth(), y);
            }
        };

        // ── TOP BAR
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 180, 230, 200),
                        getWidth(), 0, new Color(80, 40, 220, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setPreferredSize(new Dimension(0, 58));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("⬡  TechCommerce");
        logo.setFont(new Font("Monospaced", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        JLabel emailLbl = new JLabel(userEmail, SwingConstants.RIGHT);
        emailLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailLbl.setForeground(new Color(200, 235, 255));
        JLabel roleLbl = new JLabel("[ " + userRole.toUpperCase() + " ]", SwingConstants.RIGHT);
        roleLbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        roleLbl.setForeground(userRole.equals("Admin") ? new Color(255, 200, 50) : new Color(0, 240, 180));
        userInfo.add(emailLbl);
        userInfo.add(roleLbl);

        JButton logoutBtn = makeGhostButton("Logout");
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightBar.setOpaque(false);
        rightBar.add(userInfo);
        rightBar.add(logoutBtn);

        topBar.add(logo, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ── GRID NAV
        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        JLabel sectionTitle = new JLabel("Quick Access");
        sectionTitle.setFont(new Font("Monospaced", Font.BOLD, 13));
        sectionTitle.setForeground(TEXT_DIM);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        navWrap.add(sectionTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 16, 16));
        grid.setOpaque(false);

        // Row 1 – always visible
        grid.add(makeCard("Browse Products", "🛍",
                "Explore our full catalog of tech products",
                new Color(0, 180, 255), e -> new ProductListFrame(userRole).setVisible(true)));

        grid.add(makeCard("Shopping Cart", "🛒",
                "View items in your cart & checkout",
                new Color(255, 150, 0), e -> new ShoppingCartFrame(userId).setVisible(true)));

        // Row 2
        grid.add(makeCard("My Orders", "📦",
                "Track and manage your order history",
                new Color(0, 200, 130), e -> new OrdersFrame(userEmail , userId).setVisible(true)));

        grid.add(makeCard("Product Reviews", "⭐",
                "Read and write product reviews",
                new Color(180, 80, 255), e -> new ReviewsFrame(userId).setVisible(true)));

        // Row 3 – role-based
        if (userRole.equals("Admin")) {
            grid.add(makeCard("Add Product", "➕",
                    "Add a new product to the catalog",
                    new Color(0, 200, 255), e -> new AddProductFrame().setVisible(true)));
            grid.add(makeCard("Manage Inventory", "📊",
                    "Monitor stock levels and alerts",
                    new Color(100, 100, 200), e -> new InventoryFrame(userId).setVisible(true)));
        } else {
            grid.add(makeCard("My Profile", "👤",
                    "View and update your account details",
                    new Color(0, 200, 255), e -> new ProfileFrame(userEmail , userId).setVisible(true)));
            grid.add(makeCard("Track Shipment", "🚚",
                    "Real-time tracking for your orders",
                    new Color(100, 100, 200), e -> new TrackShipmentFrame(userId).setVisible(true)));
        }

        navWrap.add(grid, BorderLayout.CENTER);
        root.add(navWrap, BorderLayout.CENTER);

        // ── STATUS BAR
        JPanel statusBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(14, 18, 32));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        statusBar.setPreferredSize(new Dimension(0, 28));
        statusBar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        JLabel statusLbl = new JLabel("TechCommerce Platform ", SwingConstants.LEFT);
        statusLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLbl.setForeground(TEXT_DIM);
        statusBar.add(statusLbl, BorderLayout.WEST);
        root.add(statusBar, BorderLayout.SOUTH);

        add(root);
    }

    // ── Card factory ─────────────────────────────────────────────
    private JPanel makeCard(String title, String icon, String desc,
                            Color accent, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) { action.actionPerformed(null); }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? new Color(30, 40, 65) : new Color(18, 24, 42);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // left accent bar
                g2.setColor(hovered ? accent : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140));
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                // border
                g2.setColor(hovered ? accent : BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));

        JLabel iconLbl = new JLabel(icon + "  " + title);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        iconLbl.setForeground(accent);

        JLabel descLbl = new JLabel("<html>" + desc + "</html>");
        descLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_DIM);

        card.add(iconLbl, BorderLayout.NORTH);
        card.add(descLbl, BorderLayout.CENTER);
        return card;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }


}
