package org.example.atm_maven_jfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // Путь к базе данных
    private static final String DB_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb";
    // Путь к SQL-скрипту для создания базы данных
    private static final String SQL_SCRIPT_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/create_db.sql";

    /**
     * Метод для проверки существования базы данных и её создания при необходимости
     */
    public static void checkAndCreateDatabase() {
        File dbFile = new File(DB_PATH);

        // Проверяем, существует ли файл базы данных
        if (!dbFile.exists()) {
            // Показываем диалоговое окно с запросом на создание базы данных
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("База данных не найдена");
            alert.setHeaderText("База данных ATM_MODEL_DBASE.fdb не существует.");
            alert.setContentText("Хотите создать новую базу данных?");

            // Получаем ответ пользователя
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        createDatabase();
                        showAlert("Успех", "База данных успешно создана!");
                    } catch (Exception e) {
                        showAlert("Ошибка", "Не удалось создать базу данных: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    showAlert("Отмена", "Создание базы данных отменено.");
                }
            });
        } else {
            System.out.println("База данных уже существует.");
        }
    }

    /**
     * Метод для создания базы данных с использованием SQL-скрипта
     */
    private static void createDatabase() throws IOException, SQLException {
        // Чтение SQL-скрипта из файла
        String sqlScript = new String(Files.readAllBytes(Paths.get(SQL_SCRIPT_PATH)));

        // JDBC URL с параметром для создания новой базы данных
        String jdbcUrl = "jdbc:firebirdsql://localhost/3050:" + DB_PATH + "?create=true";
        String user = "SYSDBA";
        String password = "010802"; // Убедитесь, что пароль соответствует вашей конфигурации

        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
             Statement statement = connection.createStatement()) {

            // Разделяем скрипт на отдельные команды и выполняем их
            String[] commands = sqlScript.split(";");
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    // Игнорируем команды SET TERM, так как они не поддерживаются через JDBC
                    if (!command.trim().toUpperCase().startsWith("SET TERM")) {
                        statement.execute(command.trim());
                    }
                }
            }
        }
    }

    /**
     * Вспомогательный метод для показа информационного окна
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}