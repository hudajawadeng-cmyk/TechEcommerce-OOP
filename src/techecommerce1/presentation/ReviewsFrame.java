package techecommerce1.presentation;

import techecommerce1.dal.DatabaseManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import static techecommerce1.presentation.LoginFrame.*;

public class ReviewsFrame extends JFrame {

    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> productBox;
    private JComboBox<String> ratingBox;
    private JTextArea reviewArea;
    private String userId;
    private DatabaseManager db;

    public ReviewsFrame(String userId) {
        this.userId = userId;
        this.db = DatabaseManager.getInstance();
        setTitle("TechCommerce — Product Reviews");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadReviews();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("⭐  Product Reviews");
        title.setFont(new Font("Monospaced", Font.BOLD, 20));
        title.setForeground(new Color(170, 120, 0));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);

        String[] cols = {"Product","Reviewer","Rating","Comment","Date","Helpful"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reviewTable = new JTable(tableModel);
        reviewTable.setRowHeight(38);
        reviewTable.setBackground(new Color(255,255,255));
        reviewTable.setForeground(TEXT_MAIN);
        reviewTable.setGridColor(new Color(222,225,232));
        reviewTable.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reviewTable.setSelectionBackground(new Color(200,150,0,140));
        reviewTable.setShowVerticalLines(false);

        JTableHeader th = reviewTable.getTableHeader();
        th.setBackground(new Color(130, 100, 0));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced", Font.BOLD, 14));
        th.setPreferredSize(new Dimension(0, 40));

        // Stars renderer
        reviewTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setHorizontalAlignment(CENTER); l.setOpaque(true);
                int stars = v == null ? 0 : (int) v;
                l.setText("★".repeat(stars) + "☆".repeat(5-stars));
                l.setForeground(new Color(255,200,0));
                l.setBackground(sel ? new Color(200,150,0,140) :
                        (r%2==0 ? new Color(255,255,255) : new Color(240,242,247)));
                return l;
            }
        });
        reviewTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                if (c == 2) return reviewTable.getColumnModel().getColumn(2)
                        .getCellRenderer().getTableCellRendererComponent(t,v,sel,foc,r,c);
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(200,150,0,140) :
                        (r%2==0 ? new Color(255,255,255) : new Color(240,242,247)));
                l.setForeground(TEXT_MAIN);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });
        int[] widths = {140,100,80,200,90,60};
        for (int i=0; i<widths.length; i++) reviewTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(reviewTable);
        scroll.getViewport().setBackground(new Color(255,255,255));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));

        // Write panel
        JPanel writePanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        writePanel.setOpaque(false);
        writePanel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        writePanel.setPreferredSize(new Dimension(240,0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6,0,6,0);
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 1;

        JLabel panelTitle = new JLabel("Write a Review");
        panelTitle.setFont(new Font("Monospaced",Font.BOLD,16));
        panelTitle.setForeground(new Color(170,120,0));
        writePanel.add(panelTitle, gc);

        gc.gridy++;
        JLabel prodLbl = new JLabel("Product:"); prodLbl.setForeground(TEXT_DIM);
        prodLbl.setFont(new Font("Monospaced",Font.PLAIN,13));
        writePanel.add(prodLbl, gc);

        gc.gridy++;
        productBox = new JComboBox<>(new String[]{"Dell XPS 15","MacBook Pro 14",
                "Cisco Switch 24P","Samsung 1TB SSD","Arduino Mega","HP ProBook 450"});
        productBox.setBackground(new Color(235,238,244)); productBox.setForeground(TEXT_MAIN);
        productBox.setFont(new Font("Monospaced",Font.PLAIN,14));
        writePanel.add(productBox, gc);

        gc.gridy++;
        JLabel ratingLbl = new JLabel("Rating:"); ratingLbl.setForeground(TEXT_DIM);
        ratingLbl.setFont(new Font("Monospaced",Font.PLAIN,13));
        writePanel.add(ratingLbl, gc);

        gc.gridy++;
        ratingBox = new JComboBox<>(new String[]{"★★★★★ (5)","★★★★☆ (4)","★★★☆☆ (3)","★★☆☆☆ (2)","★☆☆☆☆ (1)"});
        ratingBox.setBackground(new Color(235,238,244)); ratingBox.setForeground(new Color(255,200,0));
        ratingBox.setFont(new Font("Monospaced",Font.PLAIN,14));
        writePanel.add(ratingBox, gc);

        gc.gridy++;
        JLabel comLbl = new JLabel("Your Review:"); comLbl.setForeground(TEXT_DIM);
        comLbl.setFont(new Font("Monospaced",Font.PLAIN,13));
        writePanel.add(comLbl, gc);

        gc.gridy++; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        reviewArea = new JTextArea(5,18);
        reviewArea.setBackground(new Color(235,238,244)); reviewArea.setForeground(TEXT_MAIN);
        reviewArea.setCaretColor(new Color(200,160,0));
        reviewArea.setFont(new Font("Monospaced",Font.PLAIN,14));
        reviewArea.setLineWrap(true); reviewArea.setWrapStyleWord(true);
        reviewArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C), BorderFactory.createEmptyBorder(5,7,5,7)));
        writePanel.add(new JScrollPane(reviewArea) {{ setBorder(BorderFactory.createEmptyBorder()); }}, gc);

        gc.gridy++; gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        JButton submitBtn = makeAccentButton("⭐  Submit Review", new Color(190,140,0), BG_DARK);
        submitBtn.addActionListener(e -> submitReview());
        writePanel.add(submitBtn, gc);

        gc.gridy++;
        JButton helpBtn = makeGhostButton("👍  Mark Helpful");
        helpBtn.setPreferredSize(new Dimension(180,32));
        helpBtn.addActionListener(e -> markHelpful());
        writePanel.add(helpBtn, gc);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, writePanel);
        split.setDividerLocation(540); split.setDividerSize(6);
        split.setBorder(null); split.setBackground(BG_DARK);
        root.add(split, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);
        add(root);
    }

    // ── DATA ─────────────────────────────────────────────────────
    private void loadReviews() {
        tableModel.setRowCount(0);
        if (db.isConnected()) {
            List<Object[]> rows = db.getReviews();
            if (!rows.isEmpty()) {
                for (Object[] r : rows) tableModel.addRow(r);
                return;
            }
        }
        // Fallback demo data
        Object[][] demo = {
                {"Dell XPS 15",     "alice@x.com", 5,"Incredible performance, worth every penny!", "2026-05-12",14},
                {"Dell XPS 15",     "bob@y.com",   4,"Great but runs warm under load.",             "2026-05-20", 8},
                {"MacBook Pro 14",  "carol@z.com", 5,"Best laptop I have ever owned.",              "2026-05-22",21},
                {"Samsung 1TB SSD", "dave@a.com",  4,"Blazing fast, easy install.",                "2026-05-15", 6},
                {"Cisco Switch 24P","eve@b.com",   3,"Reliable but the UI is outdated.",            "2026-04-30", 3},
                {"Arduino Mega",    "frank@c.com", 5,"Perfect for IoT projects!",                  "2026-05-05",11},
                {"HP ProBook 450",  "grace@d.com", 2,"Battery life is disappointing.",              "2026-06-01", 2},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }

    // ── HANDLERS ─────────────────────────────────────────────────
    private void submitReview() {
        String productName = (String) productBox.getSelectedItem();
        int    rating      = 5 - ratingBox.getSelectedIndex();
        String comment     = reviewArea.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please write a comment.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Save to DB
        if (db.isConnected() && userId != null) {
            String productId = resolveProductId(productName);
            if (productId != null) db.addReview(userId, productId, rating, comment);
        }
        // Add to table
        tableModel.insertRow(0, new Object[]{productName, "you@me.com", rating, comment,
                LocalDate.now().toString(), 0});
        reviewArea.setText("");
        JOptionPane.showMessageDialog(this,"Review submitted! Thank you. ⭐","Success",JOptionPane.INFORMATION_MESSAGE);
    }

    private void markHelpful() {
        int sel = reviewTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select a review first.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        String productName   = tableModel.getValueAt(sel,0).toString();
        String reviewerEmail = tableModel.getValueAt(sel,1).toString();
        if (db.isConnected()) db.markHelpful(productName, reviewerEmail);
        tableModel.setValueAt((int) tableModel.getValueAt(sel,5) + 1, sel, 5);
    }

    // Resolve product name → product_id using DB product list
    private String resolveProductId(String name) {
        if (!db.isConnected()) return null;
        for (Object[] p : db.getProducts()) {
            if (p[1].toString().equals(name)) return p[0].toString();
        }
        return null;
    }
}