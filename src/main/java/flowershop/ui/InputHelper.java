package flowershop.ui;

import flowershop.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public long readLong(String prompt) {
        String raw = readLine(prompt);
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new ValidationException("Значение должно быть целым числом: \"" + raw + "\"");
        }
    }

    public int readInt(String prompt) {
        String raw = readLine(prompt);
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new ValidationException("Значение должно быть целым числом: \"" + raw + "\"");
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        String raw = readLine(prompt);
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new ValidationException("Значение должно быть числом: \"" + raw + "\"");
        }
    }

    public LocalDate readDate(String prompt) {
        String raw = readLine(prompt + " (формат ГГГГ-ММ-ДД): ");
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Некорректная дата: \"" + raw + "\". Используйте формат ГГГГ-ММ-ДД");
        }
    }

    public LocalDate readDateOptional(String prompt) {
        String raw = readLine(prompt + " (ГГГГ-ММ-ДД, Enter — пропустить): ");
        if (raw.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Некорректная дата: \"" + raw + "\". Используйте формат ГГГГ-ММ-ДД");
        }
    }

    public int readMenuChoice(String prompt) {
        return readInt(prompt);
    }
}
