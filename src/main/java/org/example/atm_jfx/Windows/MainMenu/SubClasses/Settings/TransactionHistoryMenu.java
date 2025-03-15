package org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.beans.property.SimpleStringProperty;
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
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.TransactionHistory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TransactionHistoryMenu implements TransactionHistory {
    private final Scene scene;

    public TransactionHistoryMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.scene = createScene(primaryStage, previousScene, cardNumber);
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
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));

        root.getChildren().addAll(titleLabel, tableView, backButton);
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    public ObservableList<OperationInfo> loadOperationData(String cardNumber) {
        ObservableList<OperationInfo> operationData = FXCollections.observableArrayList();
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String username = "SYSDBA";
        String password = "010802";

        String query = """
            SELECT co.CARD_NUM, co.OPERATION, co.DDATE_STAMP, co.COMMENT
            FROM CLIENT_OPERATION co
            JOIN CLIENT_CARD cc ON co.CARD_NUM = cc.NUMBER_CARD
            WHERE cc.FK_CLIENT = (SELECT FK_CLIENT FROM CLIENT_CARD WHERE NUMBER_CARD = ?)
            ORDER BY co.DDATE_STAMP DESC
            """;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String cardNum = rs.getString("CARD_NUM");
                String operation = rs.getString("OPERATION");
                Timestamp ddateStamp = rs.getTimestamp("DDATE_STAMP");
                String dateOperation = ddateStamp != null ? ddateStamp.toString() : "N/A";
                String comment = rs.getString("COMMENT");

                operationData.add(new OperationInfo(cardNum, operation, dateOperation, comment));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return operationData;
    }

    public record OperationInfo(String cardNum, String operation, String dateOperation, String comment) {
    }

    public Scene getScene() {
        return scene;
    }
}