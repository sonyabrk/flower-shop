package flowershop.ui;

import flowershop.model.Bouquet;
import flowershop.service.BouquetService;

import java.math.BigDecimal;
import java.util.List;

public class BouquetMenu {

    private final InputHelper input;
    private final BouquetService bouquetService;

    public BouquetMenu(InputHelper input, BouquetService bouquetService) {
        this.input = input;
        this.bouquetService = bouquetService;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n БУКЕТЫ ");
            System.out.println("1. Добавить букет");
            System.out.println("2. Список всех букетов");
            System.out.println("3. Найти букет по ID");
            System.out.println("4. Изменить букет");
            System.out.println("5. Удалить букет");
            System.out.println("6. Поиск букета по названию");
            System.out.println("7. Фильтр по диапазону цены");
            System.out.println("0. Назад");

            int choice = input.readMenuChoice("Выберите действие: ");

            switch (choice) {
                case 1 -> create();
                case 2 -> printAll(bouquetService.getAll());
                case 3 -> printOne(bouquetService.getById(input.readLong("ID букета: ")));
                case 4 -> update();
                case 5 -> delete();
                case 6 -> printAll(bouquetService.search(input.readLine("Название (часть слова): ")));
                case 7 -> filterByPrice();
                case 0 -> back = true;
                default -> System.out.println("Неизвестный пункт меню.");
            }
        }
    }

    private void create() {
        String name = input.readLine("Название букета: ");
        String description = input.readLine("Описание: ");
        BigDecimal price = input.readBigDecimal("Цена: ");

        Bouquet created = bouquetService.create(new Bouquet(name, description, price));
        System.out.println("Букет создан: " + created);
    }

    private void update() {
        long id = input.readLong("ID букета для изменения: ");
        String name = input.readLine("Новое название: ");
        String description = input.readLine("Новое описание: ");
        BigDecimal price = input.readBigDecimal("Новая цена: ");

        Bouquet updated = bouquetService.update(id, new Bouquet(name, description, price));
        System.out.println("Букет обновлён: " + updated);
    }

    private void delete() {
        long id = input.readLong("ID букета для удаления: ");
        bouquetService.delete(id);
        System.out.println("Букет удалён.");
    }

    private void filterByPrice() {
        BigDecimal min = input.readBigDecimal("Минимальная цена: ");
        BigDecimal max = input.readBigDecimal("Максимальная цена: ");
        printAll(bouquetService.filterByPriceRange(min, max));
    }

    private void printOne(Bouquet bouquet) {
        System.out.println(bouquet);
    }

    private void printAll(List<Bouquet> bouquets) {
        if (bouquets.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        bouquets.forEach(System.out::println);
    }
}
