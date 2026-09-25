package flowershop.ui;

import flowershop.model.Customer;
import flowershop.service.CustomerService;

import java.util.List;

public class CustomerMenu {

    private final InputHelper input;
    private final CustomerService customerService;

    public CustomerMenu(InputHelper input, CustomerService customerService) {
        this.input = input;
        this.customerService = customerService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- КЛИЕНТЫ ---");
            System.out.println("1. Создать клиента");
            System.out.println("2. Список всех клиентов");
            System.out.println("3. Найти клиента по ID");
            System.out.println("4. Изменить клиента");
            System.out.println("5. Удалить клиента");
            System.out.println("6. Поиск клиента (по имени/email)");
            System.out.println("0. Назад");

            int choice = input.readMenuChoice("Выберите действие: ");

            switch (choice) {
                case 1 -> create();
                case 2 -> printAll(customerService.getAll());
                case 3 -> printOne(customerService.getById(input.readLong("ID клиента: ")));
                case 4 -> update();
                case 5 -> delete();
                case 6 -> printAll(customerService.search(input.readLine("Введите имя или email: ")));
                case 0 -> back = true;
                default -> System.out.println("Неизвестный пункт меню.");
            }
        }
    }

    private void create() {
        String fullName = input.readLine("Имя клиента: ");
        String phone = input.readLine("Телефон: ");
        String email = input.readLine("Email: ");

        Customer created = customerService.create(new Customer(fullName, phone, email));
        System.out.println("Клиент создан: " + created);
    }

    private void update() {
        long id = input.readLong("ID клиента для изменения: ");
        String fullName = input.readLine("Новое имя: ");
        String phone = input.readLine("Новый телефон: ");
        String email = input.readLine("Новый email: ");

        Customer updated = customerService.update(id, new Customer(fullName, phone, email));
        System.out.println("Клиент обновлён: " + updated);
    }

    private void delete() {
        long id = input.readLong("ID клиента для удаления: ");
        customerService.delete(id);
        System.out.println("Клиент удалён.");
    }

    private void printOne(Customer customer) {
        System.out.println(customer);
    }

    private void printAll(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        customers.forEach(System.out::println);
    }
}