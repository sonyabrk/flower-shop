package flowershop.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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

    public String readNonBlank(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            if (!raw.isBlank()) {
                return raw;
            }
            System.out.println("Ошибка: поле не может быть пустым. Введите значение.");
        }
    }

    public long readLong(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Long.parseLong(raw);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число (например, 5). Вы ввели: \"" + raw + "\"");
            }
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число (например, 5). Вы ввели: \"" + raw + "\"");
            }
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Ошибка: число должно быть больше нуля.");
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            String raw = readLine(prompt).replace(',', '.');
            try {
                return new BigDecimal(raw);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести число (например, 1500 или 1500.50). Вы ввели: \"" + raw + "\"");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (ГГГГ-ММ-ДД): ");
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверная дата \"" + raw + "\". Пример правильного формата: 2026-09-30");
            }
        }
    }

    public LocalDate readDateOptional(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (ГГГГ-ММ-ДД, Enter — пропустить): ");
            if (raw.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверная дата \"" + raw + "\". Пример правильного формата: 2026-09-30");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String raw = readLine(prompt + " (да/нет): ").toLowerCase();
            if (raw.equals("да") || raw.equals("д") || raw.equals("yes") || raw.equals("y")) {
                return true;
            }
            if (raw.equals("нет") || raw.equals("н") || raw.equals("no") || raw.equals("n")) {
                return false;
            }
            System.out.println("Ошибка: ответьте «да» или «нет».");
        }
    }

    public <E extends Enum<E>> E readEnum(String prompt, Class<E> type) {
        System.out.println("Доступные значения: " + Arrays.toString(type.getEnumConstants()));
        while (true) {
            String raw = readLine(prompt).toUpperCase();
            try {
                return Enum.valueOf(type, raw);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: значения \"" + raw + "\" нет в списке. Выберите одно из перечисленных.");
            }
        }
    }

    public int readMenuChoice(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите номер пункта меню цифрой. Вы ввели: \"" + raw + "\"");
            }
        }
    }
}