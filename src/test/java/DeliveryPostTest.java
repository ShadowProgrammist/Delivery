import java.time.LocalDateTime;
import java.util.*;

// Класс с константами платежных систем (без enum)
class PaymentSystem {
    public static final String SBERPAY = "SberPay";
    public static final String MIRPAY = "Mir Pay";
    public static final String APPLEPAY = "Apple Pay";

    public static boolean isValid(String system) {
        return SBERPAY.equals(system) || MIRPAY.equals(system) || APPLEPAY.equals(system);
    }
}


class Payment {

    private final String paymentSystem;

    public Payment(String paymentSystem) {
        if (!PaymentSystem.isValid(paymentSystem)) {
            throw new IllegalArgumentException("Неверная платежная система: " + paymentSystem);
        }
        this.paymentSystem = paymentSystem;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

}

