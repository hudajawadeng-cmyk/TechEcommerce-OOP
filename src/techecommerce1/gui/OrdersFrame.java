package techecommerce1.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import static techecommerce1.gui.LoginFrame.*;

/**
 * My Orders screen – view, filter, and cancel orders.
 */
public class OrdersFrame extends JFrame {

    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterBox;
    private String userEmail;

    public OrdersFrame(String userEmail) {
        this.userEmail = userEmail;
        setTitle("TechCommerce — My Orders");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadOrders();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel title = new JLabel("📦  My Orders");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(0, 200, 130));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterRow.setOpaque(false);
        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setForeground(TEXT_DIM);
        filterLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        filterBox = new JComboBox<>(new String[]{"All", "Processing", "Shipped", "Delivered", "Cancelled"});
        filterBox.setBackground(new Color(22, 28, 48));
        filterBox.setForeground(TEXT_MAIN);
        filterBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        filterBox.addActionListener(e -> applyFilter());

        filterRow.add(filterLbl);
        filterRow.add(filterBox);
        header.add(title, BorderLayout.WEST);
        header.add(filterRow, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Order ID","Date","Product(s)","Total ($)","Status","Payment","Action"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(34);
        ordersTable.setBackground(new Color(14,18,32));
        ordersTable.setForeground(TEXT_MAIN);
        ordersTable.setGridColor(new Color(30,40,70));
        ordersTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ordersTable.setSelectionBackground(new Color(0,160,130,80));
        ordersTable.setSelectionForeground(Color.WHITE);
        ordersTable.setShowVerticalLines(false);

        JTableHeader th = ordersTable.getTableHeader();
        th.setBackground(new Color(0, 140, 90));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 36));

        // Status renderer
        ordersTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setHorizontalAlignment(CENTER);
                l.setOpaque(true);
                String s = v == null ? "" : v.toString();
                switch (s) {
                    case "Delivered":  l.setForeground(SUCCESS_C); l.setBackground(new Color(0,50,30)); break;
                    case "Shipped":    l.setForeground(ACCENT);    l.setBackground(new Color(0,40,60)); break;
                    case "Processing": l.setForeground(new Color(255,200,50)); l.setBackground(new Color(50,45,0)); break;
                    case "Cancelled":  l.setForeground(ERROR_C);   l.setBackground(new Color(50,10,15)); break;
                    default:           l.setForeground(TEXT_DIM);  l.setBackground(BG_DARK);
                }
                return l;
            }
        });

        // Alt row renderer
        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                if (c == 4) return ordersTable.getColumnModel().getColumn(4)
                        .getCellRenderer().getTableCellRendererComponent(t,v,sel,foc,r,c);
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(0,120,90,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                l.setForeground(sel ? Color.WHITE : TEXT_MAIN);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(ordersTable);
        scroll.getViewport().setBackground(new Color(14,18,32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        root.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnRow.setOpaque(false);

        JButton detailBtn = makeAccentButton("🔍  Details", new Color(0,200,130), BG_DARK);
        detailBtn.addActionListener(e -> showOrderDetails());

        JButton cancelBtn = makeAccentButton("✖  Cancel Order", ERROR_C, BG_DARK);
        cancelBtn.addActionListener(e -> cancelOrder());

        JButton reorderBtn = makeAccentButton("🔁  Reorder", new Color(100,80,220), BG_DARK);
        reorderBtn.addActionListener(e -> reorder());

        JButton closeBtn = makeGhostButton("Close");
        closeBtn.addActionListener(e -> dispose());

        btnRow.add(detailBtn);
        btnRow.add(cancelBtn);
        btnRow.add(reorderBtn);
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        add(root);
    }

    private void loadOrders() {
        Object[][] data = {
                {"ORD-2026-001","2026-05-10","Dell XPS 15",    "1299.99","Delivered", "Paid"},
                {"ORD-2026-002","2026-05-18","Samsung 1TB SSD x2","259.98","Delivered","Paid"},
                {"ORD-2026-003","2026-05-28","MacBook Pro 14", "1999.00","Shipped",   "Paid"},
                {"ORD-2026-004","2026-06-01","Cisco Switch 24P","450.00","Processing","Pending"},
                {"ORD-2026-005","2026-06-03","Arduino Mega x3",  "115.50","Processing","Pending"},
                {"ORD-2026-006","2026-04-15","HP ProBook 450",  "899.00","Cancelled", "Refunded"},
        };
        for (Object[] r : data) {
            Object[] row = new Object[7];
            System.arraycopy(r, 0, row, 0, r.length);
            row[6] = "—";
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String filter = (String) filterBox.getSelectedItem();
        tableModel.setRowCount(0);
        loadOrders();
        if ("All".equals(filter)) return;
        for (int i = tableModel.getRowCount()-1; i >= 0; i--) {
            if (!tableModel.getValueAt(i,4).toString().equals(filter))
                tableModel.removeRow(i);
        }
    }

    private int getSelected() {
        int sel = ordersTable.getSelectedRow();
        if (sel == -1) JOptionPane.showMessageDialog(this,"Select an order first.","Warning",JOptionPane.WARNING_MESSAGE);
        return sel;
    }

    private void showOrderDetails() {
        int sel = getSelected(); if (sel == -1) return;
        String info = String.format(
                "Order ID : %s\nDate     : %s\nProduct  : %s\nTotal    : $%s\nStatus   : %s\nPayment  : %s",
                tableModel.getValueAt(sel,0), tableModel.getValueAt(sel,1),
                tableModel.getValueAt(sel,2), tableModel.getValueAt(sel,3),
                tableModel.getValueAt(sel,4), tableModel.getValueAt(sel,5));
        JOptionPane.showMessageDialog(this, info, "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelOrder() {
        int sel = getSelected(); if (sel == -1) return;
        String status = tableModel.getValueAt(sel,4).toString();
        if (status.equals("Delivered") || status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this,"Cannot cancel a " + status.toLowerCase() + " order.","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Cancel order " + tableModel.getValueAt(sel,0) + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            tableModel.setValueAt("Cancelled", sel, 4);
            tableModel.setValueAt("Refunded",  sel, 5);
        }
    }

    private void reorder() {
        int sel = getSelected(); if (sel == -1) return;
        JOptionPane.showMessageDialog(this,
                tableModel.getValueAt(sel,2) + " added to cart for reorder! 🛒",
                "Reorder", JOptionPane.INFORMATION_MESSAGE);
    }
}
