package flowershop.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import flowershop.model.Bouquet;
import flowershop.model.BouquetOrder;
import flowershop.model.Customer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public final class ExcelExporter {

    private ExcelExporter() {
    }

    public static void export(String filePath,
                               List<Customer> customers,
                               List<Bouquet> bouquets,
                               List<BouquetOrder> orders) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            writeCustomersSheet(workbook, customers);
            writeBouquetsSheet(workbook, bouquets);
            writeOrdersSheet(workbook, orders);

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                workbook.write(out);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при экспорте данных в Excel: " + e.getMessage(), e);
        }
    }

    private static void writeCustomersSheet(XSSFWorkbook workbook, List<Customer> customers) {
        Sheet sheet = workbook.createSheet("Клиенты");

        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Имя", "Телефон", "Email"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIndex = 1;
        for (Customer c : customers) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getFullName());
            row.createCell(2).setCellValue(c.getPhone());
            row.createCell(3).setCellValue(c.getEmail());
        }

        autoSizeColumns(sheet, columns.length);
    }

    private static void writeBouquetsSheet(XSSFWorkbook workbook, List<Bouquet> bouquets) {
        Sheet sheet = workbook.createSheet("Букеты");

        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Название", "Описание", "Цена"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIndex = 1;
        for (Bouquet b : bouquets) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(b.getId());
            row.createCell(1).setCellValue(b.getName());
            row.createCell(2).setCellValue(b.getDescription());
            row.createCell(3).setCellValue(b.getPrice().doubleValue());
        }

        autoSizeColumns(sheet, columns.length);
    }

    private static void writeOrdersSheet(XSSFWorkbook workbook, List<BouquetOrder> orders) {
        Sheet sheet = workbook.createSheet("Заказы");

        Row header = sheet.createRow(0);
        String[] columns = {"ID", "ID клиента", "ID букета", "Количество",
                "Сумма", "Статус", "Дата заказа", "Дата доставки"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIndex = 1;
        for (BouquetOrder o : orders) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(o.getId());
            row.createCell(1).setCellValue(o.getCustomerId());
            row.createCell(2).setCellValue(o.getBouquetId());
            row.createCell(3).setCellValue(o.getQuantity());
            row.createCell(4).setCellValue(o.getTotalPrice().doubleValue());
            row.createCell(5).setCellValue(o.getStatus().name());
            row.createCell(6).setCellValue(o.getOrderDate().toString());

            Cell deliveryCell = row.createCell(7);
            if (o.getDeliveryDate() != null) {
                deliveryCell.setCellValue(o.getDeliveryDate().toString());
            } else {
                deliveryCell.setCellValue("-");
            }
        }

        autoSizeColumns(sheet, columns.length);
    }

    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
