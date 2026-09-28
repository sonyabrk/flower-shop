package flowershop.ui;

import flowershop.model.Bouquet;
import flowershop.model.BouquetOrder;
import flowershop.model.Customer;
import flowershop.service.BouquetService;
import flowershop.service.CustomerService;
import flowershop.service.OrderService;

import java.util.ArrayList;
import java.util.List;

public class TablePrinter {

    private static final int MAX_CELL_WIDTH = 35;

    private final CustomerService customerService;
    private final BouquetService bouquetService;
    private final OrderService orderService;

    public TablePrinter(CustomerService customerService,
                        BouquetService bouquetService,
                        OrderService orderService) {
        this.customerService = customerService;
        this.bouquetService = bouquetService;
        this.orderService = orderService;
    }

    public void showAll() {
        printCustomers();
        printBouquets();
        printOrders();
    }

    private void printCustomers() {
        List<String[]> rows = new ArrayList<>();
        for (Customer c : customerService.getAll()) {
            rows.add(new String[]{
                    String.valueOf(c.getId()), c.getFullName(), c.getPhone(), c.getEmail()});
        }
        print("customers", new String[]{"id", "full_name", "phone", "email"}, rows);
    }

    private void printBouquets() {
        List<String[]> rows = new ArrayList<>();
        for (Bouquet b : bouquetService.getAll()) {
            rows.add(new String[]{
                    String.valueOf(b.getId()), b.getName(), b.getDescription(), String.valueOf(b.getPrice())});
        }
        print("bouquets", new String[]{"id", "name", "description", "price"}, rows);
    }

    private void printOrders() {
        List<String[]> rows = new ArrayList<>();
        for (BouquetOrder o : orderService.getAll()) {
            rows.add(new String[]{
                    String.valueOf(o.getId()),
                    String.valueOf(o.getCustomerId()),
                    String.valueOf(o.getBouquetId()),
                    String.valueOf(o.getQuantity()),
                    String.valueOf(o.getTotalPrice()),
                    String.valueOf(o.getStatus()),
                    String.valueOf(o.getOrderDate()),
                    o.getDeliveryDate() == null ? "-" : o.getDeliveryDate().toString()});
        }
        print("orders", new String[]{"id", "customer_id", "bouquet_id", "quantity",
                "total_price", "status", "order_date", "delivery_date"}, rows);
    }

    private void print(String title, String[] headers, List<String[]> rows) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                row[i] = cut(row[i]);
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        System.out.println("\nТаблица: " + title + " (записей: " + rows.size() + ")");
        String separator = separator(widths);
        System.out.println(separator);
        System.out.println(formatRow(headers, widths));
        System.out.println(separator);
        for (String[] row : rows) {
            System.out.println(formatRow(row, widths));
        }
        System.out.println(separator);
    }

    private String cut(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > MAX_CELL_WIDTH ? value.substring(0, MAX_CELL_WIDTH - 3) + "..." : value;
    }

    private String separator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        return sb.toString();
    }

    private String formatRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            sb.append(" ").append(String.format("%-" + widths[i] + "s", cells[i])).append(" |");
        }
        return sb.toString();
    }
}