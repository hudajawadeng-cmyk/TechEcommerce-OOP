package techecommerce1.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import static techecommerce1.gui.LoginFrame.*;

/**
 * Shopping Cart — dark theme, quantity editing, promo codes.
 */
public class ShoppingCartFrame extends JFrame {

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JLabel discountLabel;
    private JTextField promoField;

    public ShoppingCartFrame() {
        setTitle("TechCommerce — Shopping Cart");
        setSize(680, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadSampleCart();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("🛒  Shopping Cart");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(255, 150, 0));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);

        // Table — Qty column is editable
        String[] cols = {"ID", "Product", "Unit Price ($)", "Qty", "Subtotal ($)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 3 && row >= 0) {
                try {
                    int qty = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                    if (qty <= 0) { tableModel.removeRow(row); } else {
                        double unit = (double) tableModel.getValueAt(row, 2);
                        tableModel.setValueAt(Math.round(unit * qty * 100.0) / 100.0, row, 4);
                    }
                    updateTotal();
                } catch (NumberFormatException ex) { tableModel.setValueAt(1, row, 3); }
            }
        });

        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(34);
        cartTable.setBackground(new Color(14,18,32));
        cartTable.setForeground(TEXT_MAIN);
        cartTable.setGridColor(new Color(30,40,70));
        cartTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        cartTable.setSelectionBackground(new Color(200,100,0,60));
        cartTable.setSelectionForeground(Color.WHITE);
        cartTable.setShowVerticalLines(false);

        JTableHeader th = cartTable.getTableHeader();
        th.setBackground(new Color(180, 100, 0));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 36));

        cartTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(160,80,0,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                l.setForeground(sel ? Color.WHITE : (c==3 ? new Color(255,200,100) : TEXT_MAIN));
                l.setHorizontalAlignment(c >= 2 ? SwingConstants.CENTER : SwingConstants.LEFT);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.getViewport().setBackground(new Color(14,18,32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        root.add(scroll, BorderLayout.CENTER);

        // ── Bottom section
        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);

        // Promo + totals row
        JPanel promoRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        promoRow.setOpaque(false);

        promoField = new JTextField(12);
        promoField.setBackground(new Color(22,28,48));
        promoField.setForeground(TEXT_MAIN);
        promoField.setCaretColor(ACCENT);
        promoField.setFont(new Font("Monospaced", Font.PLAIN, 12));
        promoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C),
                BorderFactory.createEmptyBorder(4,8,4,8)));

        JButton promoBtn = makeAccentButton("Apply Code", new Color(60,60,180), BG_DARK);
        promoBtn.setPreferredSize(new Dimension(110, 30));
        promoBtn.addActionListener(e -> applyPromo());

        discountLabel = new JLabel("Discount: $0.00");
        discountLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        discountLabel.setForeground(SUCCESS_C);

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255,150,0));

        JLabel promoLbl = new JLabel("Promo:");
        promoLbl.setForeground(TEXT_DIM);
        promoLbl.setFont(new Font("Monospaced", Font.PLAIN, 11));

        promoRow.add(promoLbl);
        promoRow.add(promoField);
        promoRow.add(promoBtn);
        promoRow.add(Box.createHorizontalStrut(20));
        promoRow.add(discountLabel);
        promoRow.add(Box.createHorizontalStrut(10));
        promoRow.add(totalLabel);
        bottom.add(promoRow, BorderLayout.NORTH);

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        btnRow.setOpaque(false);

        JButton removeBtn = makeAccentButton("🗑  Remove", ERROR_C, BG_DARK);
        removeBtn.addActionListener(e -> handleRemove());

        JButton checkoutBtn = makeAccentButton("✅  Checkout", new Color(0,180,90), BG_DARK);
        checkoutBtn.addActionListener(e -> handleCheckout());

        JButton clearBtn = makeGhostButton("Clear All");
        clearBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,"Clear entire cart?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                tableModel.setRowCount(0); updateTotal();
            }
        });

        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(removeBtn);
        btnRow.add(checkoutBtn);
        btnRow.add(clearBtn);
        btnRow.add(closeBtn);
        bottom.add(btnRow, BorderLayout.SOUTH);

        root.add(bottom, BorderLayout.SOUTH);
        add(root);
    }

    private void loadSampleCart() {
        tableModel.addRow(new Object[]{"P001","Dell XPS 15",    1299.99, 1, 1299.99});
        tableModel.addRow(new Object[]{"P004","Samsung 1TB SSD", 129.99, 2,  259.98});
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++)
            total += (double) tableModel.getValueAt(i, 4);
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void applyPromo() {
        String code = promoField.getText().trim().toUpperCase();
        if (code.equals("TECH10")) {
            discountLabel.setText("Discount: 10% applied ✔");
            JOptionPane.showMessageDialog(this,"Promo TECH10 applied — 10% off!","Promo",JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("SAVE50")) {
            discountLabel.setText("Discount: $50 off ✔");
            JOptionPane.showMessageDialog(this,"Promo SAVE50 applied — $50 off!","Promo",JOptionPane.INFORMATION_MESSAGE);
        } else {
            discountLabel.setText("Discount: Invalid code");
            discountLabel.setForeground(ERROR_C);
        }
    }

    private void handleRemove() {
        int sel = cartTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select an item.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        tableModel.removeRow(sel);
        updateTotal();
    }

    private void handleCheckout() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,"Your cart is empty!","Empty",JOptionPane.WARNING_MESSAGE); return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Confirm checkout?\n" + totalLabel.getText(),
                "Checkout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            updateTotal();
            JOptionPane.showMessageDialog(this,
                    "🎉 Order placed! You'll receive a confirmation email.",
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
