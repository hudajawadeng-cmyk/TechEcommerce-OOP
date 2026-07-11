package techecommerce1.application;

import techecommerce1.presentation.LoginFrame; // مثال لربط المنطق

public class AuthService {
    public boolean login(String email, String password) {
        // هنا يتم استدعاء DAL للتحقق من قاعدة البيانات
        return email.endsWith("@example.com"); // منطق تجريبي
    }
}
