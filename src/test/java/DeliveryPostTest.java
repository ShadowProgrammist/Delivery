import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

public class DeliveryPostTest {

    private PaymentServer server;

    // Этот метод будет выполняться перед каждым тестом,
    // создавая новый экземпляр PaymentServer для чистоты тестов
    @BeforeEach
    public void setUp() {
        server = new PaymentServer();
    }

    // Проверяем корректность метода isValid для всех валидных и невалидных платежных систем
    @Test
    public void testValidPaymentSystems() {
        assertTrue(PaymentSystem.isValid(PaymentSystem.SBERPAY));  // Ожидаем true для SBERPAY
        assertTrue(PaymentSystem.isValid(PaymentSystem.MIRPAY));   // Ожидаем true для MIRPAY
        assertTrue(PaymentSystem.isValid(PaymentSystem.APPLEPAY)); // Ожидаем true для APPLEPAY
        assertFalse(PaymentSystem.isValid("InvalidPay"));          // Ожидаем false для несуществующей системы
    }

    // Проверяем расчет стоимости без дополнительных наценок (кроме базовой наценки платежной системы)
    @Test
    public void testCalculateCostNoModifiers() {
        double base = 1000;
        // Для SBERPAY базовая +10%, без срочности, веса и расстояния
        double cost = server.calculateCost(PaymentSystem.SBERPAY, base, false, 5, 100);
        assertEquals(1100.00, cost); // 1000 * 1.10 = 1100
    }

    // Проверяем расчет стоимости со всеми наценками одновременно
    @Test
    public void testCalculateCostAllModifiers() {
        double base = 1000;
        // Наценки: SBERPAY +10%, срочность +40%, вес >10кг +15%, расстояние >500км +12%
        double expected = 1000 * 1.10 * 1.40 * 1.15 * 1.12;
        expected = Math.round(expected * 100.0) / 100.0; // округление
        double cost = server.calculateCost(PaymentSystem.SBERPAY, base, true, 15, 600);
        assertEquals(expected, cost);
    }

    // Проверяем создание платежа, корректность полей и сохранение в истории
    @Test
    public void testProcessPaymentAndHistory() {
        double base = 1500;
        Payment p = server.processPayment(PaymentSystem.MIRPAY, base, true, 12, 700);

        assertNotNull(p); // Платеж должен быть создан
        assertEquals(PaymentSystem.MIRPAY, p.getPaymentSystem()); // Проверяем платежную систему
        assertTrue(p.isExpressDelivery()); // Проверяем флаг срочности
        assertEquals(12, p.getWeightKg()); // Проверяем вес
        assertEquals(700, p.getDistanceKm()); // Проверяем расстояние

        // Итоговая сумма должна совпадать с расчетом сервера
        double expectedFinal = server.calculateCost(PaymentSystem.MIRPAY, base, true, 12, 700);
        assertEquals(expectedFinal, p.getFinalAmount());

        // Проверяем, что платеж сохранён в истории
        List<Payment> history = server.getPaymentHistory();
        assertEquals(1, history.size());
        assertEquals(p, history.get(0));
    }

    // Проверяем корректность работы возврата платежа
    @Test
    public void testRefundPayment() {
        Payment p = server.processPayment(PaymentSystem.SBERPAY, 1000, false, 5, 100);
        assertFalse(p.isRefunded()); // Сначала платеж не возвращён

        server.refundPayment(p.getPaymentId()); // Выполняем возврат
        assertTrue(p.isRefunded()); // Проверяем, что флаг возврата установлен

        // Повторный возврат не должен изменить состояние и не должен вызвать ошибку
        server.refundPayment(p.getPaymentId());
        assertTrue(p.isRefunded());
    }

    // Проверяем, что при использовании невалидной платежной системы выбрасывается исключение
    @Test
    public void testPaymentInvalidSystem() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            server.calculateCost("123", 1000, false, 5, 100);
        });
        assertTrue(thrown.getMessage().contains("Неверная платежная система"));

        assertThrows(IllegalArgumentException.class, () -> {
            server.processPayment("123", 1000, false, 5, 100);
        });
    }
}


