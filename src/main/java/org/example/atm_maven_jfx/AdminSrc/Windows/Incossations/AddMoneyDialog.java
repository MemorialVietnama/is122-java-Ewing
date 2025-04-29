package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AddMoneyDialog {
    private final Stage dialogStage;
    private final Stage primaryStage;
    private final String atmId;
    private final Scene nextScene;
    private final Map<String, TextField> denominationFields = new HashMap<>();
    private final String[] denominations = {"50", "100", "200", "500", "1000", "2000", "5000"};

    // Измененный конструктор с добавлением параметров
    public AddMoneyDialog(Stage parentStage, Stage primaryStage, String atmId, Scene nextScene) {
        this.primaryStage = primaryStage;
        this.atmId = atmId;
        this.nextScene = nextScene;

        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);  // Исправлено WINDOW_MODAL
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Добавление денег в банкомат");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));

        // Create input fields for each denomination
        for (String denom : denominations) {
            HBox hbox = new HBox(10);
            Label label = new Label(denom + " руб:");
            TextField textField = new TextField();
            textField.setPromptText("Количество");
            textField.setText("0");
            denominationFields.put(denom, textField);

            hbox.getChildren().addAll(label, textField);
            mainLayout.getChildren().add(hbox);
        }

        // Create buttons
        HBox buttonBox = new HBox(10);
        Button autoFillButton = new Button("Авто (по 10 купюр)");
        autoFillButton.setOnAction(_ -> autoFillDenominations());

        Button addButton = new Button("Добавить");
        addButton.setOnAction(_ -> addMoneyToATM());

        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(_ -> dialogStage.close());

        buttonBox.getChildren().addAll(autoFillButton, addButton, cancelButton);
        mainLayout.getChildren().add(buttonBox);

        Scene scene = new Scene(mainLayout, 300, 400);
        dialogStage.setScene(scene);
    }

    private void autoFillDenominations() {
        for (String denom : denominations) {
            denominationFields.get(denom).setText("10");
        }
    }

    private void addMoneyToATM() {
        Connection connection = null;
        try {
            connection = DatabaseService.getConnection();
            connection.setAutoCommit(false);

            for (String denom : denominations) {
                TextField field = denominationFields.get(denom);
                int count;

                try {
                    count = Integer.parseInt(field.getText());
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Некорректное количество для номинала " + denom);
                    return;
                }

                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        String cashId = "BN_" + denom + "_" + System.currentTimeMillis() + "_" + i;
                        String serialNumber = "SN_" + denom + "_" + (int)(Math.random() * 1000000);

                        try (PreparedStatement stmt = connection.prepareStatement(
                                "INSERT INTO ATM_CASH_STORAGE (ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER) " +
                                        "VALUES (?, ?, ?, ?)")) {
                            stmt.setString(1, cashId);
                            stmt.setString(2, atmId);
                            stmt.setString(3, denom);
                            stmt.setString(4, serialNumber);
                            stmt.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();
            dialogStage.close();

            // Переход на сцену LoadMoney с анимацией загрузки
            Scene loadingScene = LoadMoney.createScene(primaryStage, nextScene);
            primaryStage.setScene(loadingScene);

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showAlert("Ошибка базы данных",
                    "Не удалось добавить деньги: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAndWait() {
        dialogStage.showAndWait();
    }
}