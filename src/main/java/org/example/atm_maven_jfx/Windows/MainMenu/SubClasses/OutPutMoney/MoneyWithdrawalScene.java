package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MoneyWithdrawalScene {
    private final Scene scene;
    private final String cardNumber;
    private final int amount;
    private final boolean isLargeBills;
    private List<CashStorage> filteredCashStorageList;
    private SessionWarning sessionWarning; // Поле для SessionWarning

    public MoneyWithdrawalScene(Stage primaryStage, Scene previousScene, String cardNumber, int amount, boolean isLargeBills) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.isLargeBills = isLargeBills;
        this.scene = createScene(primaryStage, previousScene);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label titleLabel = new Label("Заберите ваши деньги");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        TableView<CashStorage> cashStorageTable = new TableView<>();

        TableColumn<CashStorage, String> denominationColumn = new TableColumn<>("Номинал");
        denominationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDenomination())
        );
        denominationColumn.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;");

        TableColumn<CashStorage, String> serialNumberColumn = new TableColumn<>("Серийный номер");
        serialNumberColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSerialNumber())
        );
        serialNumberColumn.setStyle("-fx-font-size: 20px; -fx-alignment: CENTER;");

        cashStorageTable.setPrefWidth(800);
        cashStorageTable.setMaxWidth(800);
        cashStorageTable.getColumns().add(denominationColumn);
        cashStorageTable.getColumns().add(serialNumberColumn);

        List<CashStorage> cashStorageList;
        try {
            cashStorageList = DatabaseService.getCashStorageData();
        } catch (SQLException e) {
            e.printStackTrace();
            cashStorageList = new ArrayList<>();
        }

        filteredCashStorageList = filterCashStorageByAmount(cashStorageList, amount, isLargeBills);

        ObservableList<CashStorage> cashStorageData = FXCollections.observableArrayList(filteredCashStorageList);
        cashStorageTable.setItems(cashStorageData);

        Button doneButton = new Button("Готово");
        doneButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 20px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);

        doneButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            try {
                DatabaseService.deleteIssuedBills(filteredCashStorageList);
                DatabaseService.updateCardBalance(cardNumber, amount);

                Stage blockStage = new Stage();
                BlockWindow blockWindow = new BlockWindow();
                blockWindow.start(blockStage);
                primaryStage.hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });

        root.setTranslateX(-400);
        root.setTranslateY(-300);
        root.getChildren().addAll(titleLabel, cashStorageTable, doneButton);
        return new Scene(root, 1920, 1080);
    }

    private List<CashStorage> filterCashStorageByAmount(List<CashStorage> cashStorageList, int amount, boolean isLargeBills) {
        List<CashStorage> filteredList = new ArrayList<>();
        int remainingAmount = amount;

        // Сортировка списка купюр по номиналу
        cashStorageList.sort((a, b) -> {
            int denomA = Integer.parseInt(a.getDenomination());
            int denomB = Integer.parseInt(b.getDenomination());
            return isLargeBills ? denomB - denomA : denomA - denomB;
        });

        // Фильтрация купюр
        for (CashStorage cashStorage : cashStorageList) {
            int denomination = Integer.parseInt(cashStorage.getDenomination());

            if (denomination <= remainingAmount) {
                filteredList.add(cashStorage);
                remainingAmount -= denomination;
            }

            if (remainingAmount == 0) {
                break;
            }
        }

        if (remainingAmount > 0) {
            throw new IllegalArgumentException("Недостаточно купюр для выдачи запрошенной суммы.");
        }

        return filteredList;
    }

    public Scene getScene() {
        return scene;
    }

    public static class CashStorage {
        private final String denomination;
        private final String serialNumber;

        public CashStorage(String denomination, String serialNumber) {
            this.denomination = denomination;
            this.serialNumber = serialNumber;
        }

        public String getDenomination() {
            return denomination;
        }

        public String getSerialNumber() {
            return serialNumber;
        }
    }
}