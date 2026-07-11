package techecommerce1.application;

import techecommerce1.dal.DatabaseManager;

import java.util.List;

/**
 * Context في نمط Strategy — وواجهة الاستراتيجية معرّفة داخلها مباشرة
 *
 * يعتمد على الميثودات الحقيقية الموجودة في DatabaseManager:
 *   - getBestSellingProducts() -> List<Object[]> {name, total_sold}
 *   - getLowStockProducts()    -> List<Object[]> {name, stock_quantity}
 *   - getTotalRevenue()        -> double
 */
public class ReportService {

    private final DatabaseManager db = DatabaseManager.getInstance();

    /** واجهة الاستراتيجية (Strategy) */
    public interface ReportStrategy {
        String generate();
    }

    private ReportStrategy strategy;

    /** تحديد نوع التقرير المطلوب قبل التنفيذ */
    public void setStrategy(ReportStrategy strategy) {
        this.strategy = strategy;
    }

    /** تنفيذ التقرير حسب الاستراتيجية المختارة حالياً */
    public String generateReport() {
        if (strategy == null) {
            throw new IllegalStateException("لم يتم تحديد نوع التقرير قبل الاستدعاء (setStrategy)");
        }
        return strategy.generate();
    }

    // ── الاستراتيجيات الجاهزة، كل وحدة ترجع lambda تطبق ReportStrategy ──

    public ReportStrategy salesStrategy() {
        return () -> {
            List<Object[]> topSelling = db.getBestSellingProducts(); // {name, total_sold}
            double totalRevenue = db.getTotalRevenue();

            StringBuilder sb = new StringBuilder();
            sb.append("=== تقرير المبيعات ===\n\n");
            sb.append("إجمالي الإيرادات: ").append(totalRevenue).append(" د.ل\n\n");
            sb.append("أكثر المنتجات مبيعاً:\n");
            if (topSelling != null && !topSelling.isEmpty()) {
                for (Object[] row : topSelling) {
                    sb.append("- ").append(row[0])
                            .append(" (عدد المبيعات: ").append(row[1]).append(")\n");
                }
            } else {
                sb.append("لا توجد بيانات مبيعات حالياً.\n");
            }
            return sb.toString();
        };
    }

    public ReportStrategy inventoryStrategy() {
        return () -> {
            List<Object[]> lowStock = db.getLowStockProducts(); // {name, stock_quantity}

            StringBuilder sb = new StringBuilder();
            sb.append("=== تقرير حالة المخزون ===\n\n");
            sb.append("تنبيهات المخزون المنخفض:\n");
            if (lowStock != null && !lowStock.isEmpty()) {
                for (Object[] row : lowStock) {
                    sb.append("- ").append(row[0])
                            .append(" (الكمية المتبقية: ").append(row[1]).append(")\n");
                }
            } else {
                sb.append("لا توجد تنبيهات حالياً، المخزون بحالة جيدة.\n");
            }
            return sb.toString();
        };
    }

    // ── دوال مساعدة تبقى متاحة إن احتجت البيانات الخام دون تنسيق تقرير كامل ──

    public List<Object[]> getTopSellingProducts() {
        return db.getBestSellingProducts();
    }

    public List<Object[]> getLowStockAlerts() {
        return db.getLowStockProducts();
    }

    public double getTotalRevenue() {
        return db.getTotalRevenue();
    }
}