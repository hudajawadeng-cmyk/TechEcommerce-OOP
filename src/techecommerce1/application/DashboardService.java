package techecommerce1.application;

import techecommerce1.dal.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject في نمط Observer.
 * بدل ما نزيد كلاس Subject منفصل، DashboardService نفسها تلعب هذا الدور
 * لأنها أصلاً الجهة اللي تتابع إحصائيات الطلبات/المبيعات في الداشبورد.
 *
 * أي واجهة (مثل OrdersFrame أو TrackShipmentFrame) تقدر تسجل نفسها
 * كمراقب (OrderObserver) وتتعلم أوتوماتيكياً كل ما تتبدل حالة طلب.
 *
 * كل الأرقام دبا حقيقية من قاعدة البيانات (مش قيم وهمية ثابتة).
 */
public class DashboardService {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final List<OrderObserver> observers = new ArrayList<>();

    /** إجمالي عدد المنتجات في الكتالوج */
    public int getTotalProducts() {
        return db.getTotalProductsCount();
    }

    /** إجمالي المبيعات (الإيرادات) من كل الطلبات */
    public double getTotalSales() {
        return db.getTotalRevenue();
    }

    /** إجمالي عدد الطلبات في النظام */
    public int getTotalOrders() {
        return db.getTotalOrdersCount();
    }

    /** إجمالي عدد العملاء المسجلين */
    public int getTotalCustomers() {
        return db.getTotalCustomersCount();
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
     * تعلم كل المراقبين المسجلين — الأرقام تتحسب حية من DB عند كل استدعاء getter.
     */
    public void onOrderStatusChanged(String trackingId, String newStatus) {
        notifyObservers(trackingId, newStatus);
    }

    private void notifyObservers(String trackingId, String newStatus) {
        for (OrderObserver observer : observers) {
            observer.update(trackingId, newStatus);
        }
    }
}