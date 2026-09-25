package flowershop.ui;

import flowershop.exception.BusinessException;
import flowershop.exception.DatabaseConnectionException;
import flowershop.exception.EntityNotFoundException;
import flowershop.exception.ValidationException;
import flowershop.service.BouquetService;
import flowershop.service.CustomerService;
import flowershop.service.OrderService;

import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner;
    private final InputHelper input;
    private final CustomerMenu customerMenu;
    private final BouquetMenu bouquetMenu;
    private final OrderMenu orderMenu;

    public ConsoleMenu(CustomerService customerService,
                        BouquetService bouquetService,
                        OrderService orderService) {
        this.scanner = new Scanner(System.in);
        this.input = new InputHelper(scanner);
        this.customerMenu = new CustomerMenu(input, customerService);
        this.bouquetMenu = new BouquetMenu(input, bouquetService);
        this.orderMenu = new OrderMenu(input, orderService, customerService, bouquetService);
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMenu();
            try {
                int choice = input.readMenuChoice("Выберите действие: ");

                switch (choice) {
                    case 1 -> customerMenu.show();
                    case 2 -> bouquetMenu.show();
                    case 3 -> orderMenu.show();
                    case 4 -> orderMenu.showSearch();
                    case 5 -> orderMenu.showFilter();
                    case 6 -> orderMenu.showStatistics();
                    case 7 -> orderMenu.exportToExcel();
                    case 0 -> {
                        running = false;
                        System.out.println("Выход из программы.");
                    }
                    default -> System.out.println("Неизвестный пункт меню. Попробуйте снова.\n");
                }

            } catch (ValidationException e) {
                System.out.println("Ошибка ввода: " + e.getMessage() + "\n");
            } catch (BusinessException e) {
                System.out.println("Нарушено бизнес-правило: " + e.getMessage() + "\n");
            } catch (EntityNotFoundException e) {
                System.out.println("Не найдено: " + e.getMessage() + "\n");
            } catch (DatabaseConnectionException e) {
                System.out.println("Ошибка базы данных: " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    private void printMenu() {
        System.out.println(" ");
        System.out.println("          ЦВЕТОЧНЫЙ МАГАЗИН");
        System.out.println(" ");
        System.out.println("1. Клиенты");
        System.out.println("2. Букеты");
        System.out.println("3. Заказы");
        System.out.println("4. Поиск заказов");
        System.out.println("5. Фильтрация заказов");
        System.out.println("6. Статистика");
        System.out.println("7. Экспорт данных");
        System.out.println("0. Выход");
    }
}
