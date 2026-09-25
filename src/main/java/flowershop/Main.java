package flowershop;

import flowershop.exception.DatabaseConnectionException;
import flowershop.repository.BouquetRepository;
import flowershop.repository.BouquetRepositoryJdbc;
import flowershop.repository.CustomerRepository;
import flowershop.repository.CustomerRepositoryJdbc;
import flowershop.repository.OrderRepository;
import flowershop.repository.OrderRepositoryJdbc;
import flowershop.service.BouquetService;
import flowershop.service.CustomerService;
import flowershop.service.OrderService;
import flowershop.ui.ConsoleMenu;
import flowershop.util.DatabaseManager;

public class Main {

    public static void main(String[] args) {

        System.out.println("Запуск информационной системы \"Цветочный магазин\"...\n");

        try {
            DatabaseManager.testConnection();
        } catch (DatabaseConnectionException e) {
            System.out.println("Не удалось подключиться к базе данных: " + e.getMessage());
            System.out.println("Проверьте config.properties и убедитесь, что PostgreSQL запущен.");
            return;
        }

        // Repository-слой (JDBC-реализации)
        CustomerRepository customerRepository = new CustomerRepositoryJdbc();
        BouquetRepository bouquetRepository = new BouquetRepositoryJdbc();
        OrderRepository orderRepository = new OrderRepositoryJdbc();

        // Service-слой (бизнес-логика поверх репозиториев)
        CustomerService customerService = new CustomerService(customerRepository);
        BouquetService bouquetService = new BouquetService(bouquetRepository, orderRepository);
        OrderService orderService = new OrderService(orderRepository, customerRepository, bouquetRepository);

        // UI-слой (консольное меню)
        ConsoleMenu menu = new ConsoleMenu(customerService, bouquetService, orderService);
        menu.start();
    }
}
