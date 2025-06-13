import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;

public class DeliveryPostTest {

    private PaymentServer server;

    @BeforeEach
    public void setUp() {
        server = new PaymentServer();
    }

    @Test
    public void testValidPaymentSystems() {
        assertTrue(PaymentSystem.isValid(PaymentSystem.SBERPAY));
        assertTrue(PaymentSystem.isValid(PaymentSystem.MIRPAY));
        assertTrue(PaymentSystem.isValid(PaymentSystem.APPLEPAY));
        assertFalse(PaymentSystem.isValid("InvalidPay"));
    }

    @Test
    public void testCalculateCostNoModifiers() {
        double base = 1000;
        double cost = server.calculateCost(PaymentSystem.SBERPAY, base, false, 5, 100);
        assertEquals(1100.00, cost);
    }

    @Test
    public void testCalculateCostAllModifiers() {
        double base = 1000;
        double expected = 1000 * 1.10 * 1.40 * 1.15 * 1.12;
        expected = Math.round(expected * 100.0) / 100.0;
        double cost = server.calculateCost(PaymentSystem.SBERPAY, base, true, 15, 600);
        assertEquals(expected, cost);
    }

    @Test
    public void testProcessPaymentAndHistory() {
        double base = 1500;
        Payment p = server.processPayment(PaymentSystem.MIRPAY, base, true, 12, 700);

        assertNotNull(p);
        assertEquals(PaymentSystem.MIRPAY, p.getPaymentSystem());
        assertTrue(p.isExpressDelivery());
        assertEquals(12, p.getWeightKg());
        assertEquals(700, p.getDistanceKm());

        double expectedFinal = server.calculateCost(PaymentSystem.MIRPAY, base, true, 12, 700);
        assertEquals(expectedFinal, p.getFinalAmount());

        List<Payment> history = server.getPaymentHistory();
        assertEquals(1, history.size());
        assertEquals(p, history.get(0));
    }

    @Test
    public void testRefundPayment() {
        Payment p = server.processPayment(PaymentSystem.SBERPAY, 1000, false, 5, 100);
        assertFalse(p.isRefunded());

        server.refundPayment(p.getPaymentId());
        assertTrue(p.isRefunded());

        server.refundPayment(p.getPaymentId());
        assertTrue(p.isRefunded());
    }

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


