package org.example.atm_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_jfx.Database.DatabaseService;
import org.example.atm_jfx.Functions.InfoPanel;

import java.sql.SQLException;
import java.sql.Timestamp;

public class Incossations {

    public static class CashStorage {
        private final String idCash;
        private final String idAtm;
        private final String denominations;
        private final String serialNumber;
        private final Timestamp dateInserted;

        public CashStorage(String idCash, String idAtm, String denominations, String serialNumber, Timestamp dateInserted) {
            this.idCash = idCash;
            this.idAtm = idAtm;
            this.denominations = denominations;
            this.serialNumber = serialNumber;
            this.dateInserted = dateInserted;
        }

        public String getIdCash() {
            return idCash;
        }

        public String getIdAtm() {
            return idAtm;
        }

        public String getDenominations() {
            return denominations;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public Timestamp getDateInserted() {
            return dateInserted;
        }
    }

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
        backButton.setOnAction(event -> {
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

        TableColumn<CashStorage, String> idCashColumn = new TableColumn<>("ID Cash");
        idCashColumn.setCellValueFactory(new PropertyValueFactory<>("idCash"));

        TableColumn<CashStorage, String> idAtmColumn = new TableColumn<>("ID ATM");
        idAtmColumn.setCellValueFactory(new PropertyValueFactory<>("idAtm"));

        TableColumn<CashStorage, String> denominationsColumn = new TableColumn<>("Denominations");
        denominationsColumn.setCellValueFactory(new PropertyValueFactory<>("denominations"));

        TableColumn<CashStorage, String> serialNumberColumn = new TableColumn<>("Serial Number");
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));

        TableColumn<CashStorage, Timestamp> dateInsertedColumn = new TableColumn<>("Date Inserted");
        dateInsertedColumn.setCellValueFactory(new PropertyValueFactory<>("dateInserted"));

        tableView.getColumns().addAll(idCashColumn, idAtmColumn, denominationsColumn, serialNumberColumn, dateInsertedColumn);

        // Загрузка данных из базы данных
        loadCashStorageData(tableView);

        Button refreshSumButton = new Button("Обновить сумму");
        refreshSumButton.setStyle(backButton.getStyle());
        refreshSumButton.setOnAction(event -> {
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
        vbox.setTranslateY(-400);
        vbox.setTranslateX(-500);
        vbox.setStyle("-fx-background-color: red;");

        addMoney.setOnAction(event -> {
            boolean isDataAdded = AddMoneyDialog.display(tableView);
            if (isDataAdded) {
                loadCashStorageData(tableView);
            }
        });

        removeMoney.setOnAction(event -> {
            boolean isDataRemoved = RemoveMoneyDialog.display(tableView);
            if (isDataRemoved) {
                loadCashStorageData(tableView);
            }
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
        if (totalAmount > 200_000 || totalAmount < 50_000) {
            incassationStatusLabel.setText("Требуется инкассация");
        } else {
            incassationStatusLabel.setText("Инкассация не требуется");
        }
    }
}