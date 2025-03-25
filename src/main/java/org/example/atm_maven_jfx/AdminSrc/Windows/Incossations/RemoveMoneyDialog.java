package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import java.sql.SQLException;
import java.util.Map;

public class RemoveMoneyDialog {

    public static boolean display(TableView<Incantations.CashStorage> tableView) {
        boolean[] isDataRemoved = {false};

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Убрать деньги");

        String css = """
                -fx-background-color: #ffebee;
                -fx-font-family: 'Arial';
                -fx-font-size: 14px;
                """;

        Label amountLabel = new Label("Введите сумму для снятия:");
        amountLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");
        amountField.setStyle("""
                -fx-background-color: white;
                -fx-border-color: red;
                -fx-border-radius: 5px;
                -fx-padding: 5px;
                -fx-font-size: 14px;
                """);

        Label currentCashLabel = new Label();
        currentCashLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");

        Button okButton = new Button("ОК");
        okButton.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-padding: 8px 16px;
                -fx-background-color: red;
                -fx-border-radius: 5px;
                -fx-cursor: hand;
                """);
        okButton.setOnAction(_ -> {
            try {
                int amountToRemove = Integer.parseInt(amountField.getText());
                if (amountToRemove <= 0) {
                    showAlert("Сумма должна быть больше нуля.");
                    return;
                }

                Map<Integer, Integer> cashCount = DatabaseService.getCurrentCashCount();
                if (cashCount.isEmpty()) {
                    showAlert("В банкомате нет денег.");
                    return;
                }

                int totalAmount = cashCount.entrySet().stream()
                        .mapToInt(entry -> entry.getKey() * entry.getValue())
                        .sum();
                if (totalAmount < amountToRemove) {
                    showAlert("Недостаточно средств в банкомате.");
                    return;
                }

                isDataRemoved[0] = true;
                window.close();

                Stage primaryStage = (Stage) tableView.getScene().getWindow();
                Scene removeMoneyActionScene = RemoveMoneyAction.createScene(primaryStage, tableView.getScene(), amountToRemove);
                primaryStage.setScene(removeMoneyActionScene);

            } catch (NumberFormatException ex) {
                showAlert("Введите корректную сумму.");
            } catch (SQLException ex) {
                showAlert("Ошибка базы данных.");
            }
        });

        updateCurrentCashLabel(currentCashLabel);

        VBox layout = new VBox(10, amountLabel, amountField, currentCashLabel, okButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle(css);

        Scene scene = new Scene(layout, 400, 300);
        window.setScene(scene);
        window.showAndWait();

        return isDataRemoved[0];
    }

    private static void updateCurrentCashLabel(Label label) {
        StringBuilder cashInfo = new StringBuilder("Текущее количество банкнот:\n");
        try {
            Map<Integer, Integer> cashCount = DatabaseService.getCurrentCashCount();
            if (cashCount.isEmpty()) {
                cashInfo.append("Нет данных");
            } else {
                cashCount.forEach((denomination, count) ->
                        cashInfo.append(denomination).append(" кредитов: ").append(count).append("\n"));
            }
        } catch (SQLException e) {
            cashInfo.append("Ошибка при загрузке данных.");
        }
        label.setText(cashInfo.toString());
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #ffebee; -fx-text-fill: red;");
        alert.showAndWait();
    }
}