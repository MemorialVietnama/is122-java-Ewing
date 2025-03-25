package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;

public class WithdrawalProcessor {
    private final Scene scene;
    private final Stage primaryStage;
    private final String cardNumber;
    private final double amount;
    private static final String ATM_ID = "123456";

    public WithdrawalProcessor(Stage primaryStage, String cardNumber, double amount) {
        this.primaryStage = primaryStage;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.scene = createConfirmationScene();
        processWithdrawalInDB();
    }

    private void processWithdrawalInDB() {
        String DATABASE_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb";
        String JDBC_URL = "jdbc:firebirdsql:localhost/3050:" + new File(DATABASE_PATH).getAbsolutePath();
        String USER = "SYSDBA";
        String PASSWORD = "010802";

        String sql = "UPDATE BALANCE_CARD SET BALANCE = BALANCE - ? WHERE FK_CARD = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, cardNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Banknote> getBanknotes() {
        List<Banknote> banknotes = new ArrayList<>();
        String DATABASE_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb";
        String JDBC_URL = "jdbc:firebirdsql:localhost/3050:" + new File(DATABASE_PATH).getAbsolutePath();
        String USER = "SYSDBA";
        String PASSWORD = "010802";

        String sql = "SELECT DENOMINATIONS, SERIAL_NUMBER FROM ATM_CASH_STORAGE " +
                "WHERE ID_ATM = ? ORDER BY CAST(DENOMINATIONS AS INTEGER) DESC";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ATM_ID);
            ResultSet rs = pstmt.executeQuery();
            double remainingAmount = amount;
            while (rs.next() && remainingAmount > 0) {
                String denomStr = rs.getString("DENOMINATIONS").trim();
                int denom = Integer.parseInt(denomStr);
                if (remainingAmount >= denom) {
                    banknotes.add(new Banknote(denomStr, rs.getString("SERIAL_NUMBER")));
                    remainingAmount -= denom;
                }
            }
            if (remainingAmount > 0) {
                System.out.println("Недостаточно банкнот для выдачи полной суммы. Остаток: " + remainingAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL: " + e.getMessage());
        }
        System.out.println("Выбрано банкнот: " + banknotes.size());
        return banknotes;
    }

    private Scene createConfirmationScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label titleLabel = new Label("Операция выполнена");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label infoLabel = new Label("С карты " + cardNumber + " списано " + amount + " руб.");
        infoLabel.setStyle("-fx-text-fill: white; -fx-font-family: Arial; -fx-font-size: 24px;");

        TableView<Banknote> banknoteTable = new TableView<>();
        banknoteTable.setStyle("-fx-font-family: Arial; -fx-font-size: 24px;");
        banknoteTable.setMaxWidth(400);
        TableColumn<Banknote, String> denomColumn = new TableColumn<>("Номинал");
        denomColumn.setCellValueFactory(new PropertyValueFactory<>("denomination"));
        TableColumn<Banknote, String> serialColumn = new TableColumn<>("Серия");
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        banknoteTable.getColumns().addAll(denomColumn, serialColumn);

        List<Banknote> banknotes = getBanknotes();
        banknoteTable.getItems().addAll(banknotes);
        if (banknotes.isEmpty()) {
            Label emptyLabel = new Label("Банкноты не найдены");
            emptyLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 20px;");
            root.getChildren().add(emptyLabel);
        }

        Button okButton = new Button("ОК");
        okButton.setStyle("""
            -fx-text-fill: red;
            -fx-font-size: 30px;
            -fx-padding: 10px 20px;
            -fx-border-color: white;
            -fx-font-weight: bold;
            -fx-border-width: 2px;
            -fx-background-color: white;
            -fx-cursor: hand;
        """);
        okButton.setOnAction(_ -> {
            Stage blockStage = new Stage();
            BlockWindow blockWindow = new BlockWindow();
            blockWindow.start(blockStage);
            primaryStage.hide();
        });

        root.getChildren().addAll(titleLabel, infoLabel, banknoteTable, okButton);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }

    // Отдельный публичный класс вместо вложенного
        public record Banknote(String denomination, String serialNumber) {
    }
}