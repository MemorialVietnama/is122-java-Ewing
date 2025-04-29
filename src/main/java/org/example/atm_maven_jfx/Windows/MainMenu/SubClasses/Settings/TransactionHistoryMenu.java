package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.beans.property.SimpleStringProperty;
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
import org.example.atm_maven_jfx.Functions.SessionWarning; // Импортируем SessionWarning
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.TransactionHistory;


public class TransactionHistoryMenu implements TransactionHistory {
    private final Scene scene;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public TransactionHistoryMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.scene = createScene(primaryStage, previousScene, cardNumber);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label titleLabel = new Label("История Операций");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        TableView<OperationInfo> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<OperationInfo, String> cardNumColumn = new TableColumn<>("Номер карты");
        cardNumColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().cardNum()));

        TableColumn<OperationInfo, String> operationColumn = new TableColumn<>("Операция");
        operationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().operation()));

        TableColumn<OperationInfo, String> dateOperationColumn = new TableColumn<>("Дата операции");
        dateOperationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().dateOperation()));

        TableColumn<OperationInfo, String> commentColumn = new TableColumn<>("Комментарий");
        commentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().comment()));

        tableView.getColumns().addAll(cardNumColumn, operationColumn, dateOperationColumn, commentColumn);

        ObservableList<OperationInfo> operationData = loadOperationData(cardNumber);
        tableView.setItems(operationData);
        tableView.setMaxWidth(700);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-background-color: white; -fx-cursor: hand;");
        backButton.setOnAction(_ -> {
            primaryStage.setScene(previousScene);
            sessionWarning.stopInactivityCheck();
        });

        root.getChildren().addAll(titleLabel, tableView, backButton);
        return new Scene(root, 1920, 1080);
    }

    // Внутри класса TransactionHistoryMenu

    public ObservableList<OperationInfo> loadOperationData(String cardNumber) {
        // Используем метод из DatabaseService для загрузки данных
        return DatabaseService.loadTransactionHistory(cardNumber);
    }

    public Scene getScene() {
        return scene;
    }

    public record OperationInfo(String cardNum, String operation, String dateOperation, String comment) {
    }
}