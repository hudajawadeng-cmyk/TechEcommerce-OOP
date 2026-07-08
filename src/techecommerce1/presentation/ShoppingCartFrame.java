package techecommerce1.presentation;

import techecommerce1.dal.DatabaseManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import static techecommerce1.presentation.LoginFrame.*;

public class ShoppingCartFrame extends JFrame {

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel, discountLabel;
    private JTextField promoField;
    private String userId;
    private DatabaseManager db;

    public ShoppingCartFrame(String userId) {
        this.userId = userId;
        this.db  = DatabaseManager.getInstance();
        setTitle("TechCommerce — Shopping Cart");
        setSize(740, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadCart();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("🛒  Shopping Cart");
        title.setFont(new Font("Monospaced", Font.BOLD, 20));
        title.setForeground(new Color(210,110,0));
        title.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        root.add(title, BorderLayout.NORTH);

        // Table — Qty column editable
        String[] cols = {"ID","Product","Unit Price ($)","Qty","Subtotal ($)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow(); int col = e.getColumn();
            if (col == 3 && row >= 0) {
                try {
                    int qty = Integer.parseInt(tableModel.getValueAt(row,3).toString());
                    if (qty <= 0) {
                        // Remove from DB and table
                        String pid = tableModel.getValueAt(row,0).toString();
                        if (db.isConnected() && userId != null) db.removeFromCart(userId, pid);
                        tableModel.removeRow(row);
                    } else {
                        double unit = (double) tableModel.getValueAt(row,2);
                        tableModel.setValueAt(Math.round(unit*qty*100.0)/100.0, row, 4);
                        // Update DB
                        String pid = tableModel.getValueAt(row,0).toString();
                        if (db.isConnected() && userId != null) db.updateCartQty(userId, pid, qty);
                    }
                    updateTotal();
                } catch (NumberFormatException ex) { tableModel.setValueAt(1, row, 3); }
            }
        });

        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(38);
        cartTable.setBackground(new Color(255,255,255));
        cartTable.setForeground(TEXT_MAIN);
        cartTable.setGridColor(new Color(222,225,232));
        cartTable.setFont(new Font("Monospaced", Font.PLAIN, 15));
        cartTable.setSelectionBackground(new Color(230,130,0,140));
        cartTable.setSelectionForeground(TEXT_MAIN);
        cartTable.setShowVerticalLines(false);

        JTableHeader th = cartTable.getTableHeader();
        th.setBackground(new Color(180,100,0));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced",Font.BOLD,14));
        th.setPreferredSize(new Dimension(0,40));

        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(230,130,0,140) :
                        (r%2==0 ? new Color(255,255,255) : new Color(240,242,247)));
                l.setForeground(c==3 ? new Color(180,110,0) : TEXT_MAIN);
                l.setHorizontalAlignment(c>=2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.getViewport().setBackground(new Color(255,255,255));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        root.add(scroll, BorderLayout.CENTER);

        // Bottom
        JPanel bottom = new JPanel(new BorderLayout(0,8));
        bottom.setOpaque(false);

        JPanel promoRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        promoRow.setOpaque(false);

        promoField = new JTextField(12);
        promoField.setBackground(new Color(255,255,255)); promoField.setForeground(TEXT_MAIN);
        promoField.setCaretColor(ACCENT); promoField.setFont(new Font("Monospaced",Font.PLAIN,14));
        promoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C), BorderFactory.createEmptyBorder(4,8,4,8)));

        JButton promoBtn = makeAccentButton("Apply Code", new Color(60,60,180), BG_DARK);
        promoBtn.setPreferredSize(new Dimension(110,30));
        promoBtn.addActionListener(e -> applyPromo());

        discountLabel = new JLabel("Discount: $0.00");
        discountLabel.setFont(new Font("Monospaced",Font.PLAIN,14));
        discountLabel.setForeground(SUCCESS_C);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Monospaced",Font.BOLD,18));
        totalLabel.setForeground(new Color(210,110,0));

        JLabel promoLbl = new JLabel("Promo:"); promoLbl.setForeground(TEXT_DIM);
        promoLbl.setFont(new Font("Monospaced",Font.PLAIN,13));

        promoRow.add(promoLbl); promoRow.add(promoField); promoRow.add(promoBtn);
        promoRow.add(Box.createHorizontalStrut(20));
        promoRow.add(discountLabel); promoRow.add(Box.createHorizontalStrut(10));
        promoRow.add(totalLabel);
        bottom.add(promoRow, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 4));
        btnRow.setOpaque(false);

        JButton removeBtn   = makeAccentButton("🗑  Remove",   ERROR_C,               BG_DARK);
        JButton checkoutBtn = makeAccentButton("✅  Checkout", new Color(0,180,90),   BG_DARK);
        JButton clearBtn    = makeGhostButton("Clear All");
        JButton closeBtn    = makeGhostButton("Close");

        removeBtn  .addActionListener(e -> handleRemove());
        checkoutBtn.addActionListener(e -> handleCheckout());
        clearBtn   .addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,"Clear entire cart?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                if (db.isConnected() && userId != null) db.clearCart(userId);
                tableModel.setRowCount(0); updateTotal();
            }
        });
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(removeBtn); btnRow.add(checkoutBtn);
        btnRow.add(clearBtn);  btnRow.add(closeBtn);
        bottom.add(btnRow, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);
        add(root);
    }

    // ── DATA ─────────────────────────────────────────────────────
    private void loadCart() {
        tableModel.setRowCount(0);
        if (db.isConnected() && userId != null) {
            List<Object[]> rows = db.getCart(userId);
            if (!rows.isEmpty()) {
                for (Object[] r : rows) tableModel.addRow(r);
                updateTotal();
                return;
            }
        }
        // Demo fallback
        tableModel.addRow(new Object[]{"P001","Dell XPS 15",    1299.99, 1, 1299.99});
        tableModel.addRow(new Object[]{"P004","Samsung 1TB SSD", 129.99, 2,  259.98});
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++)
            total += (double) tableModel.getValueAt(i,4);
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    // ── HANDLERS ─────────────────────────────────────────────────
    private void applyPromo() {
        String code = promoField.getText().trim().toUpperCase();
        // Try DB first
        if (db.isConnected()) {
            double[] promo = db.getPromoCode(code);
            if (promo != null) {
                if (promo[0] == 0) discountLabel.setText("Discount: " + (int)promo[1] + "% applied ✔");
                else               discountLabel.setText("Discount: $" + (int)promo[1] + " off ✔");
                discountLabel.setForeground(SUCCESS_C);
                JOptionPane.showMessageDialog(this,"Promo code applied!","Promo",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        // Fallback hardcoded codes
        if (code.equals("TECH10"))      { discountLabel.setText("Discount: 10% applied ✔"); discountLabel.setForeground(SUCCESS_C); }
        else if (code.equals("SAVE50")) { discountLabel.setText("Discount: $50 off ✔");     discountLabel.setForeground(SUCCESS_C); }
        else                            { discountLabel.setText("Invalid code");             discountLabel.setForeground(ERROR_C);   }
    }

    private void handleRemove() {
        int sel = cartTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select an item.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        String productId = tableModel.getValueAt(sel,0).toString();
        if (db.isConnected() && userId != null) db.removeFromCart(userId, productId);
        tableModel.removeRow(sel);
        updateTotal();
    }

    private void handleCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,"Your cart is empty!","Empty",JOptionPane.WARNING_MESSAGE); return;
        }
        if (JOptionPane.showConfirmDialog(this,"Confirm checkout?\n"+totalLabel.getText(),
                "Checkout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (db.isConnected() && userId != null) {
                double total = calcTotal();
                String orderId = db.placeOrder(userId, total, "Default Shipping Address");
                if (orderId != null) {
                    tableModel.setRowCount(0); updateTotal();
                    JOptionPane.showMessageDialog(this,
                            "🎉 Order " + orderId + " placed!\nYou'll receive a confirmation email.",
                            "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            // Demo fallback
            tableModel.setRowCount(0); updateTotal();
            JOptionPane.showMessageDialog(this,"🎉 Order placed! (Demo mode)","Order Confirmed",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private double calcTotal() {
        double t = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) t += (double) tableModel.getValueAt(i,4);
        return t;
    }
}