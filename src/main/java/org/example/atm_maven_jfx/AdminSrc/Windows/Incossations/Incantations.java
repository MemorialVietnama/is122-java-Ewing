package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.InfoPanel;

import java.sql.SQLException;
import java.sql.Timestamp;

public class Incantations {

    public Scene createScene(Stage primaryStage, Scene previousScene) {
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(30);

        Label titleLabel = new Label("Инкассация");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Arial Black';");

        Label atmBalanceLabel = new Label();
        atmBalanceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-family: 'Arial Black';");

        // Новый Label для статуса инкассации
        Label incassationStatusLabel = new Label();
        incassationStatusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Arial Black';");

        Button backButton = new Button("Назад");
        backButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 20px;
                    -fx-padding: 5px 10px;
                    -fx-border-color: white;
                    -fx-font-weight: bold;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);
        backButton.setOnAction(_ -> {
            System.out.println("Switching back to the previous scene...");
            if (previousScene != null) {
                primaryStage.setScene(previousScene); // Переключаемся на предыдущую сцену
            } else {
                System.out.println("Previous scene is null!");
            }
        });

        Button addMoney = new Button("Добавить");
        addMoney.setStyle(backButton.getStyle());

        Button removeMoney = new Button("Убрать");
        removeMoney.setStyle(backButton.getStyle());

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: 'Arial Black';");

        TableView<CashStorage> tableView = new TableView<>();
        tableView.setPrefSize(800, 400);
        tableView.setStyle("""
                -fx-font-family: Arial;
                -fx-font-size: 14;
                """);

// Столбцы таблицы
        TableColumn<CashStorage, String> idCashColumn = new TableColumn<>("ID Cash");
        idCashColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().idCash())
        );

        TableColumn<CashStorage, String> idAtmColumn = new TableColumn<>("ID ATM");
        idAtmColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().idAtm())
        );

        TableColumn<CashStorage, String> denominationsColumn = new TableColumn<>("Denominations");
        denominationsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().denominations())
        );

        TableColumn<CashStorage, String> serialNumberColumn = new TableColumn<>("Serial Number");
        serialNumberColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().serialNumber())
        );

        TableColumn<CashStorage, Timestamp> dateInsertedColumn = new TableColumn<>("Date Inserted");
        dateInsertedColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().dateInserted())
        );

        // Добавление столбцов в таблицу
        tableView.getColumns().addAll(
                idCashColumn, idAtmColumn, denominationsColumn, serialNumberColumn, dateInsertedColumn
        );

        loadCashStorageData(tableView);

        Button refreshSumButton = new Button("Обновить сумму");
        refreshSumButton.setStyle(backButton.getStyle());
        refreshSumButton.setOnAction(_ -> {
            try {
                int totalAmount = DatabaseService.calculateTotalAmount();
                DatabaseService.updateCurrentAmount(totalAmount, "123456"); // Замените на реальный ID банкомата
                atmBalanceLabel.setText(DatabaseService.loadAtmBalance());
                loadCashStorageData(tableView);

                // Обновление статуса инкассации
                updateIncassationStatus(totalAmount, incassationStatusLabel);
            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Ошибка при обновлении данных");
            }
        });

        try {
            atmBalanceLabel.setText(DatabaseService.loadAtmBalance());
            // Получение текущей суммы для обновления статуса инкассации
            int totalAmount = DatabaseService.calculateTotalAmount();
            updateIncassationStatus(totalAmount, incassationStatusLabel);
        } catch (SQLException e) {
            e.printStackTrace();
            atmBalanceLabel.setText("Ошибка загрузки баланса");
        }

        HBox buttonBox = new HBox(10, backButton, addMoney, removeMoney, refreshSumButton);
        buttonBox.setAlignment(Pos.CENTER);

        tableView.setPrefWidth(800);
        tableView.setMaxWidth(800);

        VBox vbox = new VBox(10, infoPanel, titleLabel, atmBalanceLabel, buttonBox, incassationStatusLabel, statusLabel, tableView);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: red;");

        addMoney.setOnAction(_ -> {
            // Создаем и показываем диалог с правильными параметрами
            AddMoneyDialog dialog = new AddMoneyDialog(
                    primaryStage,                   // родительское окно (Stage)
                    primaryStage,                   // главное окно приложения
                    "123456",                       // ID банкомата (замените на реальный)
                    createScene(primaryStage, previousScene) // сцена для возврата (текущая сцена)
            );

            dialog.showAndWait(); // показываем диалог и ждем его закрытия

            // После закрытия диалога обновляем таблицу и баланс
            loadCashStorageData(tableView);
            try {
                atmBalanceLabel.setText(DatabaseService.loadAtmBalance());
                int totalAmount = DatabaseService.calculateTotalAmount();
                updateIncassationStatus(totalAmount, incassationStatusLabel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        removeMoney.setOnAction(_ -> {
            RemoveMoneyDialog dialog = new RemoveMoneyDialog(
                    primaryStage,
                    primaryStage,
                    createScene(primaryStage, previousScene)
            );
            dialog.showAndWait();
        });

        return new Scene(vbox, 1920, 1080);
    }

    // Метод для загрузки данных в таблицу
    private void loadCashStorageData(TableView<CashStorage> tableView) {
        try {
            tableView.getItems().clear();
            tableView.getItems().addAll(DatabaseService.loadCashStorageData());
        } catch (SQLException e) {
            e.printStackTrace();
            // Логирование ошибки
        }
    }

    // Метод для обновления статуса инкассации
    private void updateIncassationStatus(int totalAmount, Label incassationStatusLabel) {
        if (totalAmount > 150000 || totalAmount < 25000) {
            incassationStatusLabel.setText("Требуется инкассация");
        } else {
            incassationStatusLabel.setText("Инкассация не требуется");
        }
    }

    public record CashStorage(String idCash, String idAtm, String denominations, String serialNumber,
                              Timestamp dateInserted) {
    }
}