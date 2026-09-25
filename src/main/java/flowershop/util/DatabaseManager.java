package flowershop.util;

import flowershop.exception.DatabaseConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseManager.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new DatabaseConnectionException(
                        "Файл конфигурации " + CONFIG_FILE + " не найден в resources");
            }
            props.load(input);

        } catch (IOException e) {
            throw new DatabaseConnectionException("Ошибка чтения файла конфигурации БД", e);
        }

        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new DatabaseConnectionException(
                    "Не заданы параметры db.url / db.user / db.password в config.properties");
        }
    }

    private DatabaseManager() {
        // утилитный класс, экземпляры не создаются
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Не удалось подключиться к базе данных", e);
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn.isValid(2)) {
                System.out.println("Подключение к базе данных успешно установлено.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка проверки подключения к базе данных", e);
        }
    }
}
