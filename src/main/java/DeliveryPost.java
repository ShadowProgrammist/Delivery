import java.time.LocalDateTime;
import java.util.*;

class PaymentSystem {
    public static final String SBERPAY = "SberPay";
    public static final String MIRPAY = "Mir Pay";
    public static final String APPLEPAY = "Apple Pay";

    public static boolean isValid(String system) {
        return SBERPAY.equals(system) || MIRPAY.equals(system) || APPLEPAY.equals(system);
    }
}

class Payment {
    private static int idCounter = 1;

    private final int paymentId;
    private final String paymentSystem;
    private final double baseAmount;
    private final boolean expressDelivery;
    private final double weightKg;
    private final double distanceKm;
    private final LocalDateTime timestamp;
    private boolean refunded;
    private double finalAmount;

    public Payment(String paymentSystem, double baseAmount, boolean expressDelivery, double weightKg, double distanceKm) {
        if (!PaymentSystem.isValid(paymentSystem)) {
            throw new IllegalArgumentException("Неверная платежная система: " + paymentSystem);
        }
        this.paymentId = idCounter++;
        this.paymentSystem = paymentSystem;
        this.baseAmount = baseAmount;
        this.expressDelivery = expressDelivery;
        this.weightKg = weightKg;
        this.distanceKm = distanceKm;
        this.timestamp = LocalDateTime.now();
        this.refunded = false;
        this.finalAmount = calculateFinalAmount();
    }

    private double calculateFinalAmount() {
        double amount = baseAmount;

        if (PaymentSystem.SBERPAY.equals(paymentSystem)) {
            amount *= 1.10;
        } else if (PaymentSystem.MIRPAY.equals(paymentSystem)) {
            amount *= 1.15;
        } else if (PaymentSystem.APPLEPAY.equals(paymentSystem)) {
            amount *= 1.35;
        }
        if (expressDelivery) {
            amount *= 1.40;
        }
        if (weightKg > 10) {
            amount *= 1.15;
        }
        if (distanceKm > 500) {
            amount *= 1.12;
        }

        return Math.round(amount * 100.0) / 100.0; // округление до 2 знаков
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public boolean isExpressDelivery() {
        return expressDelivery;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void refund() {
        if (!refunded) {
            refunded = true;
            System.out.println("Платеж #" + paymentId + " возвращён.");
        } else {
            System.out.println("Платеж #" + paymentId + " уже был возвращён.");
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + paymentId +
                ", paymentSystem='" + paymentSystem + '\'' +
                ", baseAmount=" + baseAmount +
                ", finalAmount=" + finalAmount +
                ", expressDelivery=" + expressDelivery +
                ", weightKg=" + weightKg +
                ", distanceKm=" + distanceKm +
                ", timestamp=" + timestamp +
                ", refunded=" + refunded +
                '}';
    }
}

class PaymentServer {
    private final List<Payment> paymentHistory = new ArrayList<>();

    public double calculateCost(String paymentSystem, double baseAmount, boolean expressDelivery, double weightKg, double distanceKm) {
        if (!PaymentSystem.isValid(paymentSystem)) {
            throw new IllegalArgumentException("Неверная платежная система: " + paymentSystem);
        }

        double amount = baseAmount;
        if (PaymentSystem.SBERPAY.equals(paymentSystem)) {
            amount *= 1.10;
        } else if (PaymentSystem.MIRPAY.equals(paymentSystem)) {
            amount *= 1.15;
        } else if (PaymentSystem.APPLEPAY.equals(paymentSystem)) {
            amount *= 1.35;
        }
        if (expressDelivery) {
            amount *= 1.40;
        }
        if (weightKg > 10) {
            amount *= 1.15;
        }
        if (distanceKm > 500) {
            amount *= 1.12;
        }
        return Math.round(amount * 100.0) / 100.0;
    }

    public Payment processPayment(String paymentSystem, double baseAmount, boolean expressDelivery, double weightKg, double distanceKm) {
        Payment payment = new Payment(paymentSystem, baseAmount, expressDelivery, weightKg, distanceKm);
        paymentHistory.add(payment);
        System.out.println("Платеж обработан: " + payment);
        return payment;
    }

    public void refundPayment(int paymentId) {
        for (Payment payment : paymentHistory) {
            if (payment.getPaymentId() == paymentId) {
                payment.refund();
                return;
            }
        }
        System.out.println("Платеж с ID " + paymentId + " не найден.");
    }

    public List<Payment> getPaymentHistory() {
        return Collections.unmodifiableList(paymentHistory);
    }
}

