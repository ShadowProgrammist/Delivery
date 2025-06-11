// Демонстрация использования сервиса
public class Console {
    public static void main(String[] args) {
        PaymentServer server = new PaymentServer();

        double baseCost = 1500.0;

        // Пользователь вводит параметры доставки и способ оплаты
        String paymentSystem = PaymentSystem.SBERPAY; //
        boolean expressDelivery = true; // срочная доставка
        double weightKg = 12.5; // вес посылки больше 10 кг
        double distanceKm = 600; // расстояние больше 500 км

        System.out.println("Базовая стоимость доставки: " + baseCost + " руб.");
        System.out.println("Платежная система: " + paymentSystem);
        System.out.println("Срочная доставка: " + (expressDelivery ? "Да" : "Нет"));
        System.out.println("Вес посылки: " + weightKg + " кг");
        System.out.println("Расстояние доставки: " + distanceKm + " км");

        // Пользователь запрашивает расчет стоимости с наценками
        double totalCost = server.calculateCost(paymentSystem, baseCost, expressDelivery, weightKg, distanceKm);
        System.out.println("\nИтоговая стоимость с наценками: " + totalCost + " руб.");

        // Пользователь принимает решение оплатить
        boolean userPays = true; // допустим, пользователь согласен

        if (userPays) {
            Payment payment = server.processPayment(paymentSystem, baseCost, expressDelivery, weightKg, distanceKm);
            System.out.println("Оплата прошла успешно. ID платежа: " + payment.getPaymentId());
        } else {
            System.out.println("Оплата отменена пользователем.");
        }

        // Для демонстрации можно вывести историю платежей
        System.out.println("\nИстория платежей:");
        for (Payment p : server.getPaymentHistory()) {
            System.out.println(p);
        }
    }
}