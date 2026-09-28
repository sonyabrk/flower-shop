package flowershop.ui;

import flowershop.exception.EntityNotFoundException;
import flowershop.exception.OperationCancelledException;
import flowershop.model.BouquetOrder;
import flowershop.model.OrderStatus;
import flowershop.service.BouquetService;
import flowershop.service.CustomerService;
import flowershop.service.OrderService;
import flowershop.util.ExcelExporter;

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
            if (choice == 0) {
                back = true;
                continue;
            }
            ErrorHandler.run(() -> handle(choice));
        }
    }

    private void handle(int choice) {
        switch (choice) {
            case 1 -> create();
            case 2 -> printAll(orderService.getAll());
            case 3 -> System.out.println(orderService.getById(input.readLong("ID заказа: ")));
            case 4 -> update();
            case 5 -> changeStatus();
            case 6 -> delete();
            case 7 -> sort();
            default -> System.out.println("Неизвестный пункт меню. Выберите число из списка.");
        }
    }

    // ---------- запросы ID с повтором, пока не введён существующий ----------

    private long readExistingCustomerId() {
        while (true) {
            long id = input.readLong("ID клиента (0 — отмена): ");
            if (id == 0) {
                throw new OperationCancelledException("Операция отменена.");
            }
            try {
                customerService.getById(id);
                return id;
            } catch (EntityNotFoundException e) {
                System.out.println("Ошибка: клиента с ID " + id + " не существует. Введите другой ID.");
            }
        }
    }

    private long readExistingBouquetId(String prompt) {
        while (true) {
            long id = input.readLong(prompt + " (0 — отмена): ");
            if (id == 0) {
                throw new OperationCancelledException("Операция отменена.");
            }
            try {
                bouquetService.getById(id);
                return id;
            } catch (EntityNotFoundException e) {
                System.out.println("Ошибка: букета с ID " + id + " не существует. Введите другой ID.");
            }
        }
    }

    private long readExistingOrderId(String prompt) {
        while (true) {
            long id = input.readLong(prompt + " (0 — отмена): ");
            if (id == 0) {
                throw new OperationCancelledException("Операция отменена.");
            }
            try {
                orderService.getById(id);
                return id;
            } catch (EntityNotFoundException e) {
                System.out.println("Ошибка: заказа с ID " + id + " не существует. Введите другой ID.");
            }
        }
    }

    private LocalDate readDeliveryDate(LocalDate orderDate) {
        while (true) {
            LocalDate date = input.readDateOptional("Дата доставки");
            if (date == null || !date.isBefore(orderDate)) {
                return date;
            }
            System.out.println("Ошибка: дата доставки не может быть раньше даты заказа (" + orderDate + ").");
        }
    }

    // ---------- операции ----------

    private void create() {
        long customerId = readExistingCustomerId();
        long bouquetId = readExistingBouquetId("ID букета");
        int quantity = input.readPositiveInt("Количество: ");
        LocalDate orderDate = LocalDate.now();
        LocalDate deliveryDate = readDeliveryDate(orderDate);

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
        long id = readExistingOrderId("ID заказа для изменения");
        BouquetOrder existing = orderService.getById(id);
        long bouquetId = readExistingBouquetId("Новый ID букета");
        int quantity = input.readPositiveInt("Новое количество: ");
        LocalDate deliveryDate = readDeliveryDate(existing.getOrderDate());

        BouquetOrder updated = new BouquetOrder();
        updated.setBouquetId(bouquetId);
        updated.setQuantity(quantity);
        updated.setDeliveryDate(deliveryDate);
        updated.setOrderDate(existing.getOrderDate());

        System.out.println("Заказ обновлён: " + orderService.update(id, updated));
    }

    private void changeStatus() {
        long id = readExistingOrderId("ID заказа");
        BouquetOrder current = orderService.getById(id);
        System.out.println("Текущий статус: " + current.getStatus());
        OrderStatus status = input.readEnum("Новый статус: ", OrderStatus.class);

        System.out.println("Статус изменён: " + orderService.changeStatus(id, status));
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
        if (choice != 1 && choice != 2) {
            System.out.println("Неизвестный способ сортировки. Выберите 1 или 2.");
            return;
        }
        boolean ascending = input.readYesNo("По возрастанию?");

        List<BouquetOrder> result = (choice == 1)
                ? orderService.sortByDate(ascending)
                : orderService.sortByTotalPrice(ascending);
        printAll(result);
    }

    public void showSearch() {
        System.out.println("\n--- ПОИСК ЗАКАЗОВ ---");
        System.out.println("1. По имени клиента");
        System.out.println("2. По названию букета");
        int choice = input.readMenuChoice("Выберите способ поиска: ");

        switch (choice) {
            case 1 -> printAll(orderService.searchByCustomerName(input.readNonBlank("Имя клиента: ")));
            case 2 -> printAll(orderService.searchByBouquetName(input.readNonBlank("Название букета: ")));
            default -> System.out.println("Неизвестный способ поиска. Выберите 1 или 2.");
        }
    }

    public void showFilter() {
        System.out.println("\n--- ФИЛЬТРАЦИЯ ЗАКАЗОВ ---");
        System.out.println("1. По статусу");
        System.out.println("2. По диапазону дат");
        int choice = input.readMenuChoice("Выберите фильтр: ");

        switch (choice) {
            case 1 -> printAll(orderService.filterByStatus(
                    input.readEnum("Статус: ", OrderStatus.class)));
            case 2 -> {
                LocalDate from = input.readDate("Дата с");
                LocalDate to = input.readDate("Дата по");
                while (to.isBefore(from)) {
                    System.out.println("Ошибка: конечная дата раньше начальной. Введите её ещё раз.");
                    to = input.readDate("Дата по");
                }
                printAll(orderService.filterByDateRange(from, to));
            }
            default -> System.out.println("Неизвестный фильтр. Выберите 1 или 2.");
        }
    }

    public void showStatistics() {
        System.out.println("\n--- СТАТИСТИКА ---");
        System.out.println("Всего клиентов: " + customerService.getAll().size());
        System.out.println("Всего букетов в каталоге: " + bouquetService.getAll().size());
        System.out.println("Всего заказов: " + orderService.getAll().size());

        Map<OrderStatus, Long> byStatus = orderService.countByStatus();
        byStatus.forEach((status, count) -> System.out.println("  " + status + ": " + count));

        System.out.println("Общая выручка (доставленные заказы): " + orderService.totalRevenue());
    }

    public void exportToExcel() {
        String fileName = "flower_shop_export_" + LocalDate.now() + ".xlsx";

        ExcelExporter.export(
                fileName,
                customerService.getAll(),
                bouquetService.getAll(),
                orderService.getAll()
        );

        System.out.println("Данные экспортированы в файл: " + fileName);
    }

    private void printAll(List<BouquetOrder> orders) {
        if (orders.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        orders.forEach(System.out::println);
    }
}