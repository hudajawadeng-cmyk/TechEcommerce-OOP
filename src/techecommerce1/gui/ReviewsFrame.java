package techecommerce1.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Product Reviews — browse reviews and write your own.
 */
public class ReviewsFrame extends JFrame {

    private JTable reviewTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> productBox;
    private JComboBox<String> ratingBox;
    private JTextArea reviewArea;

    public ReviewsFrame() {
        setTitle("TechCommerce — Product Reviews");
        setSize(820, 560);
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

        // Title
        JLabel title = new JLabel("⭐  Product Reviews");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(200, 150, 0));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);

        // ── LEFT: reviews table
        String[] cols = {"Product", "Reviewer", "Rating", "Comment", "Date", "Helpful"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reviewTable = new JTable(tableModel);
        reviewTable.setRowHeight(34);
        reviewTable.setBackground(new Color(14,18,32));
        reviewTable.setForeground(TEXT_MAIN);
        reviewTable.setGridColor(new Color(30,40,70));
        reviewTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reviewTable.setSelectionBackground(new Color(180,130,0,60));
        reviewTable.setShowVerticalLines(false);

        JTableHeader th = reviewTable.getTableHeader();
        th.setBackground(new Color(130, 100, 0));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 36));

        // Stars renderer for Rating column
        reviewTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setHorizontalAlignment(CENTER);
                l.setOpaque(true);
                int stars = v == null ? 0 : (int)v;
                String display = "★".repeat(stars) + "☆".repeat(5-stars);
                l.setText(display);
                l.setForeground(new Color(255, 200, 0));
                l.setBackground(sel ? new Color(100,80,0,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                return l;
            }
        });

        // Alt row renderer
        reviewTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                if (c == 2) return reviewTable.getColumnModel().getColumn(2)
                        .getCellRenderer().getTableCellRendererComponent(t,v,sel,foc,r,c);
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(120,90,0,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                l.setForeground(sel ? Color.WHITE : TEXT_MAIN);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        // Adjust column widths
        int[] widths = {140, 100, 80, 200, 90, 60};
        for (int i = 0; i < widths.length; i++)
            reviewTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(reviewTable);
        scroll.getViewport().setBackground(new Color(14,18,32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));

        // ── RIGHT: write review panel
        JPanel writePanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,22,40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        writePanel.setOpaque(false);
        writePanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        writePanel.setPreferredSize(new Dimension(240, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 0, 6, 0);
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 1;

        JLabel panelTitle = new JLabel("Write a Review");
        panelTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        panelTitle.setForeground(new Color(200, 160, 0));
        writePanel.add(panelTitle, gc);

        gc.gridy++;
        JLabel prodLbl = new JLabel("Product:");
        prodLbl.setForeground(TEXT_DIM);
        prodLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        writePanel.add(prodLbl, gc);

        gc.gridy++;
        productBox = new JComboBox<>(new String[]{"Dell XPS 15","MacBook Pro 14","Cisco Switch 24P",
                "Samsung 1TB SSD","Arduino Mega","HP ProBook 450"});
        productBox.setBackground(new Color(25,32,55));
        productBox.setForeground(TEXT_MAIN);
        productBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        writePanel.add(productBox, gc);

        gc.gridy++;
        JLabel ratingLbl = new JLabel("Rating:");
        ratingLbl.setForeground(TEXT_DIM);
        ratingLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        writePanel.add(ratingLbl, gc);

        gc.gridy++;
        ratingBox = new JComboBox<>(new String[]{"★★★★★ (5)","★★★★☆ (4)","★★★☆☆ (3)","★★☆☆☆ (2)","★☆☆☆☆ (1)"});
        ratingBox.setBackground(new Color(25,32,55));
        ratingBox.setForeground(new Color(255,200,0));
        ratingBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        writePanel.add(ratingBox, gc);

        gc.gridy++;
        JLabel commentLbl = new JLabel("Your Review:");
        commentLbl.setForeground(TEXT_DIM);
        commentLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        writePanel.add(commentLbl, gc);

        gc.gridy++; gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        reviewArea = new JTextArea(5, 18);
        reviewArea.setBackground(new Color(25,32,55));
        reviewArea.setForeground(TEXT_MAIN);
        reviewArea.setCaretColor(new Color(200,160,0));
        reviewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(5,7,5,7)));
        writePanel.add(new JScrollPane(reviewArea) {{ setBorder(BorderFactory.createEmptyBorder()); }}, gc);

        gc.gridy++; gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        JButton submitBtn = makeAccentButton("⭐  Submit Review", new Color(200,150,0), BG_DARK);
        submitBtn.addActionListener(e -> submitReview());
        writePanel.add(submitBtn, gc);

        gc.gridy++;
        JButton helpBtn = makeGhostButton("👍  Mark Helpful");
        helpBtn.setPreferredSize(new Dimension(180, 32));
        helpBtn.addActionListener(e -> markHelpful());
        writePanel.add(helpBtn, gc);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, writePanel);
        split.setDividerLocation(540);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(BG_DARK);
        root.add(split, BorderLayout.CENTER);

        // Bottom
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    private void loadReviews() {
        Object[][] data = {
                {"Dell XPS 15",      "alice@x.com", 5, "Incredible performance, worth every penny!",     "2026-05-12", 14},
                {"Dell XPS 15",      "bob@y.com",   4, "Great laptop but runs warm under load.",          "2026-05-20", 8},
                {"MacBook Pro 14",   "carol@z.com", 5, "Best laptop I have ever owned. Period.",          "2026-05-22", 21},
                {"Samsung 1TB SSD",  "dave@a.com",  4, "Blazing fast, easy install.",                    "2026-05-15", 6},
                {"Cisco Switch 24P", "eve@b.com",   3, "Reliable but the UI is outdated.",               "2026-04-30", 3},
                {"Arduino Mega",     "frank@c.com", 5, "Perfect for my IoT project, great community.",   "2026-05-05", 11},
                {"HP ProBook 450",   "grace@d.com", 2, "Battery life is disappointing.",                  "2026-06-01", 2},
        };
        for (Object[] r : data) tableModel.addRow(r);
    }

    private void submitReview() {
        String product = (String) productBox.getSelectedItem();
        int rating = 5 - ratingBox.getSelectedIndex();
        String comment = reviewArea.getText().trim();
        if (comment.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please write a comment.","Warning",JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.insertRow(0, new Object[]{product, "you@me.com", rating, comment,
                LocalDate.now().toString(), 0});
        reviewArea.setText("");
        JOptionPane.showMessageDialog(this,"Review submitted! Thank you. ⭐","Success",JOptionPane.INFORMATION_MESSAGE);
    }

    private void markHelpful() {
        int sel = reviewTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select a review first.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        int cur = (int) tableModel.getValueAt(sel, 5);
        tableModel.setValueAt(cur + 1, sel, 5);
    }
}