package techecommerce1.gui;

import techecommerce1.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import static techecommerce1.gui.LoginFrame.*;

public class ProductListFrame extends JFrame {

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private String userRole;
    private DatabaseManager db;

    public ProductListFrame(String userRole) {
        this.userRole = userRole;
        this.db  = DatabaseManager.getInstance();
        setTitle("TechCommerce — Browse Products");
        setSize(780, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0,0,14,0));

        JLabel title = new JLabel("🛍  Product Catalog");
        title.setFont(new Font("Monospaced",Font.BOLD,18));
        title.setForeground(ACCENT);
        titleBar.add(title, BorderLayout.WEST);

        JPanel searchRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        searchRow.setOpaque(false);
        searchField = new JTextField(18);
        searchField.setBackground(new Color(22,28,48)); searchField.setForeground(TEXT_MAIN);
        searchField.setCaretColor(ACCENT); searchField.setFont(new Font("Monospaced",Font.PLAIN,12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_C), BorderFactory.createEmptyBorder(4,8,4,8)));

        JButton searchBtn  = LoginFrame.makeAccentButton("Search", ACCENT, BG_DARK);
        searchBtn.setPreferredSize(new Dimension(90,32));
        JButton clearSearch = LoginFrame.makeGhostButton("Reset");
        clearSearch.setPreferredSize(new Dimension(70,32));

        searchRow.add(new JLabel("  ") {{ setForeground(TEXT_DIM); }});
        searchRow.add(searchField); searchRow.add(searchBtn); searchRow.add(clearSearch);
        titleBar.add(searchRow, BorderLayout.EAST);
        root.add(titleBar, BorderLayout.NORTH);

        String[] cols = {"ID","Name","Brand","Price ($)","Category","Stock","Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(32);
        productTable.setBackground(new Color(14,18,32)); productTable.setForeground(TEXT_MAIN);
        productTable.setGridColor(new Color(30,40,70));
        productTable.setFont(new Font("Monospaced",Font.PLAIN,12));
        productTable.setSelectionBackground(new Color(0,160,220,80));
        productTable.setSelectionForeground(Color.WHITE);
        productTable.setShowVerticalLines(false);

        JTableHeader header = productTable.getTableHeader();
        header.setBackground(new Color(10,130,200)); header.setForeground(Color.WHITE);
        header.setFont(new Font("Monospaced",Font.BOLD,12));
        header.setPreferredSize(new Dimension(0,36));

        productTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                                                                     boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setHorizontalAlignment(SwingConstants.CENTER); l.setOpaque(true);
                String s = v == null ? "" : v.toString();
                if (s.equals("In Stock"))      { l.setForeground(SUCCESS_C);           l.setBackground(new Color(0,60,40)); }
                else if (s.equals("Low Stock")){ l.setForeground(new Color(255,200,0));l.setBackground(new Color(60,50,0)); }
                else                           { l.setForeground(ERROR_C);             l.setBackground(new Color(60,10,20));}
                if (sel) l.setBackground(l.getBackground().brighter());
                return l;
            }
        });
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                                                                     boolean sel, boolean foc, int r, int c) {
                if (c == 6) return productTable.getColumnModel().getColumn(6)
                        .getCellRenderer().getTableCellRendererComponent(t,v,sel,foc,r,c);
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(0,140,200,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                l.setForeground(sel ? Color.WHITE : TEXT_MAIN);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.getViewport().setBackground(new Color(14,18,32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        root.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 12));
        btnRow.setOpaque(false);
        JButton cartBtn = makeAccentButton("🛒  Add to Cart", new Color(255,150,0), BG_DARK);
        JButton viewBtn = makeAccentButton("🔍  View Details", ACCENT, BG_DARK);
        JButton closeBtn = makeGhostButton("Close");
        cartBtn .addActionListener(e -> handleAddToCart());
        viewBtn .addActionListener(e -> handleViewDetails());
        closeBtn.addActionListener(e -> dispose());
        btnRow.add(cartBtn); btnRow.add(viewBtn);
        if (userRole.equals("Admin")) {
            JButton delBtn  = makeAccentButton("🗑  Delete", new Color(220,50,70),  BG_DARK);
            JButton editBtn = makeAccentButton("✏  Edit",   new Color(100,100,220), BG_DARK);
            delBtn .addActionListener(e -> handleDelete());
            editBtn.addActionListener(e -> showInfo("Edit Product — Coming Soon"));
            btnRow.add(delBtn); btnRow.add(editBtn);
        }
        btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        searchBtn  .addActionListener(e -> filterProducts(searchField.getText().trim()));
        clearSearch.addActionListener(e -> { searchField.setText(""); loadProducts(); });
        searchField.addActionListener(e -> filterProducts(searchField.getText().trim()));
        add(root);
    }

    // ── DATA ─────────────────────────────────────────────────────
    private void loadProducts() {
        tableModel.setRowCount(0);
        if (db.isConnected()) {
            List<Object[]> rows = db.getProducts();
            if (!rows.isEmpty()) {
                for (Object[] r : rows) tableModel.addRow(r);
                return;
            }
        }
        // Demo fallback
        Object[][] demo = {
                {"P001","Dell XPS 15",      "Dell",    1299.99,"Laptops",    15,"In Stock"},
                {"P002","MacBook Pro 14",   "Apple",   1999.00,"Laptops",     8,"In Stock"},
                {"P003","Cisco Switch 24P", "Cisco",    450.00,"Networking", 20,"In Stock"},
                {"P004","Samsung 1TB SSD",  "Samsung",  129.99,"Storage",    50,"In Stock"},
                {"P005","Arduino Mega",     "Arduino",   38.50,"Sensors",   100,"In Stock"},
                {"P006","HP ProBook 450",   "HP",        899.00,"Laptops",    3,"Low Stock"},
                {"P007","Seagate 4TB HDD",  "Seagate",   89.99,"Storage",   35,"In Stock"},
                {"P008","Raspberry Pi 5",   "RPi",        80.00,"Sensors",    0,"Out of Stock"},
                {"P009","TP-Link AX6000",   "TP-Link",  249.99,"Networking", 12,"In Stock"},
                {"P010","Intel NUC 13",     "Intel",    599.00,"Servers",     6,"Low Stock"},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }

    private void filterProducts(String kw) {
        tableModel.setRowCount(0);
        if (db.isConnected() && !kw.isEmpty()) {
            List<Object[]> rows = db.searchProducts(kw);
            if (!rows.isEmpty()) { for (Object[] r : rows) tableModel.addRow(r); return; }
        }
        loadProducts();
        if (kw.isEmpty()) return;
        String low = kw.toLowerCase();
        for (int i = tableModel.getRowCount()-1; i >= 0; i--) {
            String n = tableModel.getValueAt(i,1).toString().toLowerCase();
            String b = tableModel.getValueAt(i,2).toString().toLowerCase();
            String c = tableModel.getValueAt(i,4).toString().toLowerCase();
            if (!n.contains(low) && !b.contains(low) && !c.contains(low)) tableModel.removeRow(i);
        }
    }

    // ── HANDLERS ─────────────────────────────────────────────────
    private void handleAddToCart() {
        int sel = productTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select a product first.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        String status = tableModel.getValueAt(sel,6).toString();
        if (status.equals("Out of Stock")) { JOptionPane.showMessageDialog(this,"This product is out of stock.","Unavailable",JOptionPane.WARNING_MESSAGE); return; }
        JOptionPane.showMessageDialog(this, tableModel.getValueAt(sel,1) + " added to cart! 🛒","Cart",JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleViewDetails() {
        int sel = productTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select a product first.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        String info = String.format("ID: %s\nName: %s\nBrand: %s\nPrice: $%s\nCategory: %s\nStock: %s\nStatus: %s",
                tableModel.getValueAt(sel,0), tableModel.getValueAt(sel,1), tableModel.getValueAt(sel,2),
                tableModel.getValueAt(sel,3), tableModel.getValueAt(sel,4), tableModel.getValueAt(sel,5),
                tableModel.getValueAt(sel,6));
        JOptionPane.showMessageDialog(this,info,"Product Details",JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete() {
        int sel = productTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this,"Select a product.","Warning",JOptionPane.WARNING_MESSAGE); return; }
        String id = tableModel.getValueAt(sel,0).toString();
        String nm = tableModel.getValueAt(sel,1).toString();
        if (JOptionPane.showConfirmDialog(this,"Delete: "+nm+"?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
            if (db.isConnected()) db.deleteProduct(id);
            tableModel.removeRow(sel);
        }
    }

    private void showInfo(String msg) { JOptionPane.showMessageDialog(this,msg,"Info",JOptionPane.INFORMATION_MESSAGE); }
}