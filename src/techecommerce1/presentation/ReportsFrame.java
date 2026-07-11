package techecommerce1.presentation;

import techecommerce1.application.ReportService;
import techecommerce1.dal.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportsFrame extends JFrame {

    private DatabaseManager db;
    private ReportService reportService;

    private JButton salesBtn;
    private JButton inventoryBtn;
    private JTextArea reportArea;

    public ReportsFrame() {
        this.db = DatabaseManager.getInstance();
        this.reportService = new ReportService();

        setTitle("TechCommerce — التقارير الإحصائية");
        setSize(700, 500);
        setLocationRelativeTo(null);

        // ── تهيئة عناصر الواجهة قبل استعمالها ──
        salesBtn = new JButton("تقرير المبيعات");
        inventoryBtn = new JButton("تقرير المخزون");
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        salesBtn.addActionListener(e -> {
            reportService.setStrategy(reportService.salesStrategy());
            reportArea.setText(reportService.generateReport());
        });

        inventoryBtn.addActionListener(e -> {
            reportService.setStrategy(reportService.inventoryStrategy());
            reportArea.setText(reportService.generateReport());
        });

        // ── لوحة الأزرار العلوية ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.add(salesBtn);
        btnPanel.add(inventoryBtn);

        JPanel textReportPanel = new JPanel(new BorderLayout());
        textReportPanel.add(btnPanel, BorderLayout.NORTH);
        textReportPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        // ── التابات ──
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("تقرير نصي (Strategy)", textReportPanel);
        tabbedPane.add("أكثر المنتجات مبيعاً", createReportTable("best_selling"));
        tabbedPane.add("نقص المخزون", createReportTable("low_stock"));
        tabbedPane.add("نشاط العملاء", createReportTable("customer_activity"));

        add(tabbedPane);
    }

    private JScrollPane createReportTable(String type) {
        String[] columns = {"الاسم", "القيمة/العدد"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        java.util.List<Object[]> rows;
        switch (type) {
            case "best_selling":
                rows = db.getBestSellingProducts();
                break;
            case "low_stock":
                rows = db.getLowStockProducts();
                break;
            case "customer_activity":
                rows = db.getCustomerActivity();
                break;
            default:
                rows = null;
        }

        if (rows != null) {
            for (Object[] row : rows) {
                model.addRow(row);
            }
        }

        return new JScrollPane(new JTable(model));
    }
}