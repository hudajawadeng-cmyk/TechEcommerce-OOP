package techecommerce1.application;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject في نمط Observer.
 * بدل ما نزيد كلاس Subject منفصل، DashboardService نفسها تلعب هذا الدور
 * لأنها أصلاً الجهة اللي تتابع إحصائيات الطلبات/المبيعات في الداشبورد.
 *
 * أي واجهة (مثل OrdersFrame أو TrackShipmentFrame) تقدر تسجل نفسها
 * كمراقب (OrderObserver) وتتعلم أوتوماتيكياً كل ما تتبدل حالة طلب.
 */
public class DashboardService {

    private final List<OrderObserver> observers = new ArrayList<>();

    private int totalProducts = 50;     // سيتم استبدالها لاحقاً بجلب البيانات من DB
    private double totalSales = 1500.50;

    public int getTotalProducts() {
        return totalProducts;
    }

    public double getTotalSales() {
        return totalSales;
    }

    /** تسجيل مراقب جديد (مثلاً واجهة تعرض حالة الطلب) */
    public void registerObserver(OrderObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /** إلغاء التسجيل — يستحسن استدعاؤها عند إغلاق الواجهة لتفادي Memory Leak */
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    /**
     * تُستدعى عند تحديث حالة طلب (من DAL بعد تعديل قاعدة البيانات).
     * تحدّث إحصائيات الداشبورد ثم تعلم كل المراقبين المسجلين.
     */
    public void onOrderStatusChanged(String trackingId, String newStatus) {
        if ("مكتمل".equals(newStatus)) {
            // مكان لإعادة حساب totalSales فعلياً من DB لاحقاً
        }
        notifyObservers(trackingId, newStatus);
    }

    private void notifyObservers(String trackingId, String newStatus) {
        for (OrderObserver observer : observers) {
            observer.update(trackingId, newStatus);
        }
    }
}