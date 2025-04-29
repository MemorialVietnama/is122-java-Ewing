package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class RemoveMoneyDialog {
    private final Stage dialogStage;
    private final Stage primaryStage;
    private final Scene returnScene;
    private final Map<String, TextField> denominationFields = new HashMap<>();
    private final String[] denominations = {"50", "100", "200", "500", "1000", "2000", "5000"};

    public RemoveMoneyDialog(Stage parentStage, Stage primaryStage, Scene returnScene) {
        this.primaryStage = primaryStage;
        this.returnScene = returnScene;

        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.setTitle("Удаление денег из банкомата");

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #34495e;");

        // Поле для ввода общей суммы
        Label totalLabel = new Label("Общая сумма для удаления:");
        totalLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        TextField totalAmountField = new TextField();
        totalAmountField.setPromptText("Введите сумму");
        totalAmountField.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-radius: 5px; -fx-padding: 5px;");

        // Кнопка для автоматического распределения суммы по номиналам
        Button distributeButton = new Button("Распределить сумму");
        distributeButton.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 5px; -fx-cursor: hand;");
        distributeButton.setOnAction(_ -> distributeAmount(totalAmountField.getText()));

        // Разделитель
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ecf0f1;");

        // Поля для ручного ввода количества купюр
        Label manualLabel = new Label("Или укажите количество купюр:");
        manualLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        for (String denom : denominations) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label(denom + " руб:");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            TextField textField = new TextField();
            textField.setPromptText("Количество");
            textField.setText("0");
            textField.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-radius: 5px; -fx-padding: 5px; -fx-font-size: 14px;");
            denominationFields.put(denom, textField);

            hbox.getChildren().addAll(label, textField);
            mainLayout.getChildren().add(hbox);
        }

        // Кнопки действий
        Button removeAllButton = new Button("Удалить все");
        removeAllButton.setStyle("-fx-base: #c0392b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 5px; -fx-cursor: hand;");
        removeAllButton.setOnAction(_ -> removeAllMoney());

        Button removeSelectedButton = new Button("Удалить выбранное");
        removeSelectedButton.setStyle("-fx-base: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 5px; -fx-cursor: hand;");
        removeSelectedButton.setOnAction(_ -> removeSelectedMoney());

        Button cancelButton = new Button("Отмена");
        cancelButton.setStyle("-fx-base: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-border-radius: 5px; -fx-cursor: hand;");
        cancelButton.setOnAction(_ -> dialogStage.close());

        HBox buttonBox = new HBox(10, removeAllButton, removeSelectedButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(totalLabel, totalAmountField, distributeButton,
                separator, manualLabel, buttonBox);

        Scene scene = new Scene(mainLayout, 400, 500);
        dialogStage.setScene(scene);
    }

    private void distributeAmount(String amountText) {
        try {
            int totalAmount = Integer.parseInt(amountText);
            // Алгоритм жадного распределения суммы по номиналам
            for (int i = denominations.length - 1; i >= 0; i--) {
                String denom = denominations[i];
                int denomination = Integer.parseInt(denom);
                int count = totalAmount / denomination;
                if (count > 0) {
                    denominationFields.get(denom).setText(String.valueOf(count));
                    totalAmount %= denomination;
                } else {
                    denominationFields.get(denom).setText("0");
                }
            }
            if (totalAmount > 0) {
                showAlert(Alert.AlertType.WARNING, "Внимание",
                        "Остаток " + totalAmount + " руб. невозможно распределить по имеющимся номиналам");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректную сумму");
        }
    }

    private void removeAllMoney() {
        try (Connection connection = DatabaseService.getConnection()) {
            connection.setAutoCommit(false);

            // Получаем общую сумму перед удалением
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT SUM(CAST(DENOMINATIONS AS INTEGER)) FROM ATM_CASH_STORAGE WHERE ID_ATM = '123456'")) {
                if (rs.next()) {
                    rs.getInt(1);
                }
            }

            // Удаляем все записи
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM ATM_CASH_STORAGE WHERE ID_ATM = '123456'")) {
                int deletedRows = stmt.executeUpdate();
                connection.commit();

                if (deletedRows > 0) {
                    transitionToRemoveMoneyAction();
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Информация",
                            "В банкомате нет денег для удаления");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка",
                    "Не удалось удалить деньги: " + e.getMessage());
        }
    }

    private void removeSelectedMoney() {
        try (Connection connection = DatabaseService.getConnection()) {
            connection.setAutoCommit(false);
            boolean success = true;

            // Проверяем достаточно ли купюр каждого номинала
            for (String denom : denominations) {
                TextField field = denominationFields.get(denom);
                int count = Integer.parseInt(field.getText());

                if (count > 0) {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "SELECT COUNT(*) FROM ATM_CASH_STORAGE " +
                                    "WHERE ID_ATM = ? AND DENOMINATIONS = ?")) {
                        stmt.setString(1, "123456");
                        stmt.setString(2, denom);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) < count) {
                                showAlert(Alert.AlertType.WARNING, "Внимание",
                                        "В банкомате недостаточно купюр номиналом " + denom);
                                success = false;
                                break;
                            }
                        }
                    }
                }
            }

            if (success) {
                // Удаляем купюры
                for (String denom : denominations) {
                    TextField field = denominationFields.get(denom);
                    int count = Integer.parseInt(field.getText());

                    if (count > 0) {
                        try (PreparedStatement stmt = connection.prepareStatement(
                                "DELETE FROM ATM_CASH_STORAGE " +
                                        "WHERE ID_ATM = ? AND DENOMINATIONS = ? " +
                                        "ORDER BY DATE_INSERTED " +
                                        "ROWS ?")) {
                            stmt.setString(1, "123456");
                            stmt.setString(2, denom);
                            stmt.setInt(3, count);
                            stmt.executeUpdate();
                        }
                    }
                }
                connection.commit();

                // Переход на новую сцену
                transitionToRemoveMoneyAction();
            } else {
                connection.rollback();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Введите корректные числовые значения");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка",
                    "Не удалось удалить деньги: " + e.getMessage());
        }
    }

    private void transitionToRemoveMoneyAction() {
        dialogStage.close();
        Scene removeMoneyScene = RemoveMoneyAction.createScene(primaryStage, returnScene);
        primaryStage.setScene(removeMoneyScene);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAndWait() {
        dialogStage.showAndWait();
    }
}