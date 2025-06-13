// Демонстрация использования сервиса
public class Console {
    public static void main(String[] args) {
        PaymentServer server = new PaymentServer();

        double baseCost = 1500.0;

        String paymentSystem = PaymentSystem.SBERPAY;
        boolean expressDelivery = true;
        double weightKg = 12.5;
        double distanceKm = 600;

        System.out.println("Базовая стоимость доставки: " + baseCost + " руб.");
        System.out.println("Платежная система: " + paymentSystem);
        System.out.println("Срочная доставка: " + (expressDelivery ? "Да" : "Нет"));
        System.out.println("Вес посылки: " + weightKg + " кг");
        System.out.println("Расстояние доставки: " + distanceKm + " км");

        double totalCost = server.calculateCost(paymentSystem, baseCost, expressDelivery, weightKg, distanceKm);
        System.out.println("\nИтоговая стоимость с наценками: " + totalCost + " руб.");

        boolean userPays = true;

        if (userPays) {
            Payment payment = server.processPayment(paymentSystem, baseCost, expressDelivery, weightKg, distanceKm);
            System.out.println("Оплата прошла успешно. ID платежа: " + payment.getPaymentId());
        } else {
            System.out.println("Оплата отменена пользователем.");
        }

        System.out.println("\nИстория платежей:");
        for (Payment p : server.getPaymentHistory()) {
            System.out.println(p);
        }
    }
}