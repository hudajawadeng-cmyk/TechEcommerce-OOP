package techecommerce1.gui;

import techecommerce1.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import static techecommerce1.gui.LoginFrame.*;

public class InventoryFrame extends JFrame {

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private String userId;
    private DatabaseManager db;

    public InventoryFrame(String userId) {
        this.userId = userId;
        this.db= DatabaseManager.getInstance();
        setTitle("TechCommerce — Inventory Management");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(BG_DARK);
        initComponents();
        loadInventory();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // ── Wrapper for summary + table
        JPanel centerWrap = new JPanel(new BorderLayout(0, 14));
        centerWrap.setOpaque(false);

        // Title
        JLabel title = new JLabel("📊  Inventory Management");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(120, 120, 220));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        root.add(title, BorderLayout.NORTH);

        // Summary cards
        JPanel summary = new JPanel(new GridLayout(1, 4, 14, 0));
        summary.setOpaque(false);
        summary.add(statCard("Total SKUs",   "10", new Color(0,180,255)));
        summary.add(statCard("In Stock",      "7", new Color(0,200,130)));
        summary.add(statCard("Low Stock",     "2", new Color(255,200,0)));
        summary.add(statCard("Out of Stock",  "1", new Color(220,60,80)));
        centerWrap.add(summary, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID","Product","Brand","Category","Stock","Min Stock","Status","Value ($)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 4; }
        };

        // Stock edit → update DB
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (col == 4 && row >= 0) {
                try {
                    int newQty = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
                    String pid = tableModel.getValueAt(row, 0).toString();
                    if (db.isConnected()) db.updateStock(pid, newQty);
                } catch (NumberFormatException ignored) {}
            }
        });

        inventoryTable = new JTable(tableModel);
        inventoryTable.setRowHeight(32);
        inventoryTable.setBackground(new Color(14,18,32));
        inventoryTable.setForeground(TEXT_MAIN);
        inventoryTable.setGridColor(new Color(30,40,70));
        inventoryTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inventoryTable.setSelectionBackground(new Color(80,60,180,80));
        inventoryTable.setShowVerticalLines(false);

        JTableHeader th = inventoryTable.getTableHeader();
        th.setBackground(new Color(60, 60, 160));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Monospaced", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 36));

        inventoryTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setHorizontalAlignment(CENTER); l.setOpaque(true);
                switch (v == null ? "" : v.toString()) {
                    case "OK":           l.setForeground(SUCCESS_C);            l.setBackground(new Color(0,50,30));  break;
                    case "Low Stock":    l.setForeground(new Color(255,200,0)); l.setBackground(new Color(50,45,0)); break;
                    case "Out of Stock": l.setForeground(ERROR_C);              l.setBackground(new Color(50,10,15)); break;
                    default:             l.setForeground(TEXT_DIM);             l.setBackground(BG_DARK);
                }
                return l;
            }
        });
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int r, int c) {
                if (c == 6) return inventoryTable.getColumnModel().getColumn(6)
                        .getCellRenderer().getTableCellRendererComponent(t,v,sel,foc,r,c);
                JLabel l = (JLabel) super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setOpaque(true);
                l.setBackground(sel ? new Color(60,50,160,80) :
                        (r%2==0 ? new Color(14,18,32) : new Color(18,24,44)));
                l.setForeground(sel ? Color.WHITE : TEXT_MAIN);
                l.setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(inventoryTable);
        scroll.getViewport().setBackground(new Color(14,18,32));
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        centerWrap.add(scroll, BorderLayout.CENTER);
        root.add(centerWrap, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 12));
        btnRow.setOpaque(false);

        JButton refreshBtn = makeAccentButton("🔄  Refresh",       new Color(80,80,200),  BG_DARK);
        JButton restockBtn = makeAccentButton("📥  Restock Alert", new Color(255,200,0),  BG_DARK);
        JButton exportBtn  = makeAccentButton("📄  Export CSV",    new Color(0,160,200),  BG_DARK);
        JButton closeBtn   = makeGhostButton("Close");

        refreshBtn.addActionListener(e -> { tableModel.setRowCount(0); loadInventory(); });
        restockBtn.addActionListener(e -> showRestockAlerts());
        exportBtn .addActionListener(e -> exportCSV());
        closeBtn  .addActionListener(e -> dispose());

        btnRow.add(refreshBtn); btnRow.add(restockBtn);
        btnRow.add(exportBtn);  btnRow.add(closeBtn);
        root.add(btnRow, BorderLayout.SOUTH);
        add(root);
    }

    // ── DATA ─────────────────────────────────────────────────────
    private void loadInventory() {
        tableModel.setRowCount(0);
        if (db.isConnected()) {
            List<Object[]> rows = db.getInventory();
            if (!rows.isEmpty()) {
                for (Object[] r : rows) tableModel.addRow(r);
                return;
            }
        }
        // Fallback demo data
        Object[][] demo = {
                {"P001","Dell XPS 15",      "Dell",   "Laptops",    15, 5, "OK",          19499.85},
                {"P002","MacBook Pro 14",   "Apple",  "Laptops",     8, 3, "OK",          15992.00},
                {"P003","Cisco Switch 24P", "Cisco",  "Networking", 20, 5, "OK",           9000.00},
                {"P004","Samsung 1TB SSD",  "Samsung","Storage",    50,10, "OK",           6499.50},
                {"P005","Arduino Mega",     "Arduino","Sensors",   100,20, "OK",           3850.00},
                {"P006","HP ProBook 450",   "HP",     "Laptops",     3, 5, "Low Stock",    2697.00},
                {"P007","Seagate 4TB HDD",  "Seagate","Storage",    35,10, "OK",           3149.65},
                {"P008","Raspberry Pi 5",   "RPi",    "Sensors",     0,10, "Out of Stock",    0.00},
                {"P009","TP-Link AX6000",   "TP-Link","Networking", 12, 8, "OK",           2999.88},
                {"P010","Intel NUC 13",     "Intel",  "Servers",     4, 5, "Low Stock",    2396.00},
        };
        for (Object[] r : demo) tableModel.addRow(r);
    }

    // ── HELPERS ──────────────────────────────────────────────────
    private JPanel statCard(String label, String value, Color accent) {
        JPanel p = new JPanel(new GridLayout(2,1)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18,22,40));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(accent);
                g2.fillRoundRect(0,0,4,getHeight(),4,4);
                g2.setColor(BORDER_C);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10,16,10,10));
        JLabel num = new JLabel(value); num.setFont(new Font("Monospaced",Font.BOLD,22)); num.setForeground(accent);
        JLabel nam = new JLabel(label); nam.setFont(new Font("Monospaced",Font.PLAIN,11)); nam.setForeground(TEXT_DIM);
        p.add(num); p.add(nam);
        return p;
    }

    private void showRestockAlerts() {
        StringBuilder sb = new StringBuilder("⚠ Items needing restock:\n\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String s = tableModel.getValueAt(i,6).toString();
            if (!s.equals("OK"))
                sb.append("• ").append(tableModel.getValueAt(i,1))
                        .append(" [").append(s).append("]\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Restock Alerts", JOptionPane.WARNING_MESSAGE);
    }

    private void exportCSV() {
        StringBuilder csv = new StringBuilder("ID,Product,Brand,Category,Stock,MinStock,Status,Value\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                csv.append(tableModel.getValueAt(i,c));
                if (c < tableModel.getColumnCount()-1) csv.append(",");
            }
            csv.append("\n");
        }
        JOptionPane.showMessageDialog(this,
                "CSV ready (" + tableModel.getRowCount() + " rows).\nIn production this saves to inventory.csv",
                "Export CSV", JOptionPane.INFORMATION_MESSAGE);
    }
}