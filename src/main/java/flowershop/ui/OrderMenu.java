package flowershop.ui;

import flowershop.model.BouquetOrder;
import flowershop.model.OrderStatus;
import flowershop.service.BouquetService;
import flowershop.service.CustomerService;
import flowershop.service.OrderService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OrderMenu {

    private final InputHelper input;
    private final OrderService orderService;
    private final CustomerService customerService;
    private final BouquetService bouquetService;

    public OrderMenu(InputHelper input, OrderService orderService,
                      CustomerService customerService, BouquetService bouquetService) {
        this.input = input;
        this.orderService = orderService;
        this.customerService = customerService;
        this.bouquetService = bouquetService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- ЗАКАЗЫ ---");
            System.out.println("1. Создать заказ");
            System.out.println("2. Список всех заказов");
            System.out.println("3. Найти заказ по ID");
            System.out.println("4. Изменить заказ");
            System.out.println("5. Изменить статус заказа");
            System.out.println("6. Удалить заказ");
            System.out.println("7. Сортировка заказов");
            System.out.println("0. Назад");

            int choice = input.readMenuChoice("Выберите действие: ");

            switch (choice) {
                case 1 -> create();
                case 2 -> printAll(orderService.getAll());
                case 3 -> printOne(orderService.getById(input.readLong("ID заказа: ")));
                case 4 -> update();
                case 5 -> changeStatus();
                case 6 -> delete();
                case 7 -> sort();
                case 0 -> back = true;
                default -> System.out.println("Неизвестный пункт меню.");
            }
        }
    }

    private void create() {
        long customerId = input.readLong("ID клиента: ");
        long bouquetId = input.readLong("ID букета: ");
        int quantity = input.readInt("Количество: ");
        LocalDate orderDate = LocalDate.now();
        LocalDate deliveryDate = input.readDateOptional("Дата доставки");

        BouquetOrder order = new BouquetOrder();
        order.setCustomerId(customerId);
        order.setBouquetId(bouquetId);
        order.setQuantity(quantity);
        order.setOrderDate(orderDate);
        order.setDeliveryDate(deliveryDate);

        BouquetOrder created = orderService.create(order);
        System.out.println("Заказ создан: " + created);
    }

    private void update() {
        long id = input.readLong("ID заказа для изменения: ");
        long bouquetId = input.readLong("Новый ID букета: ");
        int quantity = input.readInt("Новое количество: ");
        LocalDate deliveryDate = input.readDateOptional("Новая дата доставки");

        BouquetOrder updated = new BouquetOrder();
        updated.setBouquetId(bouquetId);
        updated.setQuantity(quantity);
        updated.setDeliveryDate(deliveryDate);

        BouquetOrder result = orderService.update(id, updated);
        System.out.println("Заказ обновлён: " + result);
    }

    private void changeStatus() {
        long id = input.readLong("ID заказа: ");
        System.out.println("Доступные статусы: " + java.util.Arrays.toString(OrderStatus.values()));
        String statusRaw = input.readLine("Новый статус: ").toUpperCase();

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusRaw);
        } catch (IllegalArgumentException e) {
            throw new flowershop.exception.ValidationException(
                    "Некорректный статус: \"" + statusRaw + "\"");
        }

        BouquetOrder updated = orderService.changeStatus(id, status);
        System.out.println("Статус изменён: " + updated);
    }

    private void delete() {
        long id = input.readLong("ID заказа для удаления: ");
        orderService.delete(id);
        System.out.println("Заказ удалён.");
    }

    private void sort() {
        System.out.println("1. По дате заказа");
        System.out.println("2. По сумме заказа");
        int choice = input.readMenuChoice("Выберите способ сортировки: ");
        boolean ascending = input.readLine("По возрастанию? (да/нет): ").equalsIgnoreCase("да");

        List<BouquetOrder> result = switch (choice) {
            case 1 -> orderService.sortByDate(ascending);
            case 2 -> orderService.sortByTotalPrice(ascending);
            default -> {
                System.out.println("Неизвестный способ сортировки.");
                yield List.of();
            }
        };
        printAll(result);
    }

    public void showSearch() {
        System.out.println("\n--- ПОИСК ЗАКАЗОВ ---");
        System.out.println("1. По имени клиента");
        System.out.println("2. По названию букета");
        int choice = input.readMenuChoice("Выберите способ поиска: ");

        List<BouquetOrder> result = switch (choice) {
            case 1 -> orderService.searchByCustomerName(input.readLine("Имя клиента: "));
            case 2 -> orderService.searchByBouquetName(input.readLine("Название букета: "));
            default -> {
                System.out.println("Неизвестный способ поиска.");
                yield List.of();
            }
        };
        printAll(result);
    }

    public void showFilter() {
        System.out.println("\n--- ФИЛЬТРАЦИЯ ЗАКАЗОВ ---");
        System.out.println("1. По статусу");
        System.out.println("2. По диапазону дат");
        int choice = input.readMenuChoice("Выберите фильтр: ");

        List<BouquetOrder> result = switch (choice) {
            case 1 -> {
                System.out.println("Доступные статусы: " + java.util.Arrays.toString(OrderStatus.values()));
                String raw = input.readLine("Статус: ").toUpperCase();
                yield orderService.filterByStatus(OrderStatus.valueOf(raw));
            }
            case 2 -> {
                LocalDate from = input.readDate("Дата с");
                LocalDate to = input.readDate("Дата по");
                yield orderService.filterByDateRange(from, to);
            }
            default -> {
                System.out.println("Неизвестный фильтр.");
                yield List.of();
            }
        };
        printAll(result);
    }

    public void showStatistics() {
        System.out.println("\n--- СТАТИСТИКА ---");
        System.out.println("Всего клиентов: " + customerService.getAll().size());
        System.out.println("Всего букетов в каталоге: " + bouquetService.getAll().size());
        System.out.println("Всего заказов: " + orderService.getAll().size());

        Map<OrderStatus, Long> byStatus = orderService.countByStatus();
        byStatus.forEach((status, count) ->
                System.out.println("  " + status + ": " + count));

        System.out.println("Общая выручка (доставленные заказы): " + orderService.totalRevenue());
    }

    public void exportToExcel() {
        // Реализация появится вместе с ExcelExporter в util/
        System.out.println("Экспорт в Excel пока не реализован — будет добавлен на следующем шаге.");
    }

    private void printOne(BouquetOrder order) {
        System.out.println(order);
    }

    private void printAll(List<BouquetOrder> orders) {
        if (orders.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        orders.forEach(System.out::println);
    }
}
