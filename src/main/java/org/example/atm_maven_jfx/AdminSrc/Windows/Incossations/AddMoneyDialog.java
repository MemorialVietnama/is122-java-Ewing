package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;

import java.sql.SQLException;

public class AddMoneyDialog {

    public static boolean display(TableView<Incantations.CashStorage> tableView) {
        boolean[] isDataAdded = {false};
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Добавить деньги");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #f4f4f9; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        Label totalAmountLabel = new Label("Общая сумма: 0");
        totalAmountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField countField50 = new TextField("0");
        TextField countField100 = new TextField("0");
        TextField countField500 = new TextField("0");
        TextField countField1000 = new TextField("0");
        TextField countField2000 = new TextField("0");
        TextField countField5000 = new TextField("0");

        countField50.setPrefWidth(60);
        countField100.setPrefWidth(60);
        countField500.setPrefWidth(60);
        countField1000.setPrefWidth(60);
        countField2000.setPrefWidth(60);
        countField5000.setPrefWidth(60);

        Runnable updateTotal = () -> {
            try {
                int total = Integer.parseInt(countField50.getText()) * 50 +
                        Integer.parseInt(countField100.getText()) * 100 +
                        Integer.parseInt(countField500.getText()) * 500 +
                        Integer.parseInt(countField1000.getText()) * 1000 +
                        Integer.parseInt(countField2000.getText()) * 2000 +
                        Integer.parseInt(countField5000.getText()) * 5000;
                totalAmountLabel.setText("Общая сумма: " + total);
            } catch (NumberFormatException e) {
                totalAmountLabel.setText("Общая сумма: 0");
            }
        };

        countField50.textProperty().addListener((_, _, _) -> updateTotal.run());
        countField100.textProperty().addListener((_, _, _) -> updateTotal.run());
        countField500.textProperty().addListener((_, _, _) -> updateTotal.run());
        countField1000.textProperty().addListener((_, _, _) -> updateTotal.run());
        countField2000.textProperty().addListener((_, _, _) -> updateTotal.run());
        countField5000.textProperty().addListener((_, _, _) -> updateTotal.run());

        Button autoButton = new Button("Авто");
        autoButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #2196f3; -fx-text-fill: white; -fx-border-radius: 5px;");
        autoButton.setOnAction(_ -> {
            countField50.setText("10");
            countField100.setText("10");
            countField500.setText("10");
            countField1000.setText("10");
            countField2000.setText("10");
            countField5000.setText("10");
            updateTotal.run();
        });

        Button okButton = new Button("ОК");
        okButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4caf50; -fx-text-fill: white; -fx-border-radius: 5px;");
        okButton.setOnAction(_ -> {
            try {
                int count50 = Integer.parseInt(countField50.getText());
                int count100 = Integer.parseInt(countField100.getText());
                int count500 = Integer.parseInt(countField500.getText());
                int count1000 = Integer.parseInt(countField1000.getText());
                int count2000 = Integer.parseInt(countField2000.getText());
                int count5000 = Integer.parseInt(countField5000.getText());

                int total = count50 * 50 + count100 * 100 + count500 * 500 +
                        count1000 * 1000 + count2000 * 2000 + count5000 * 5000;

                if (total <= 0) {
                    showAlert("Сумма должна быть больше нуля.");
                    return;
                }

                if (count50 < 0 || count100 < 0 || count500 < 0 ||
                        count1000 < 0 || count2000 < 0 || count5000 < 0) {
                    showAlert("Количество не может быть отрицательным.");
                    return;
                }

                DatabaseService.insertCashIntoDatabase(count50, 50);
                DatabaseService.insertCashIntoDatabase(count100, 100);
                DatabaseService.insertCashIntoDatabase(count500, 500);
                DatabaseService.insertCashIntoDatabase(count1000, 1000);
                DatabaseService.insertCashIntoDatabase(count2000, 2000);
                DatabaseService.insertCashIntoDatabase(count5000, 5000);

                isDataAdded[0] = true;

                window.close();

                Stage primaryStage = (Stage) tableView.getScene().getWindow();
                Scene loadMoneyScene = LoadMoney.createScene(primaryStage,
                        LoadingLoadMoney.createScene(primaryStage, tableView.getScene()));
                primaryStage.setScene(loadMoneyScene);

            } catch (NumberFormatException ex) {
                showAlert("Введите корректные числовые значения.");
            } catch (SQLException ex) {
                showAlert("Ошибка при добавлении данных в базу.");
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(
                totalAmountLabel,
                new HBox(10, new Label("50 номинал: Кол-во:"), countField50),
                new HBox(10, new Label("100 номинал: Кол-во:"), countField100),
                new HBox(10, new Label("500 номинал: Кол-во:"), countField500),
                new HBox(10, new Label("1000 номинал: Кол-во:"), countField1000),
                new HBox(10, new Label("2000 номинал: Кол-во:"), countField2000),
                new HBox(10, new Label("5000 номинал: Кол-во:"), countField5000),
                new HBox(10, autoButton, okButton)
        );

        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.showAndWait();

        return isDataAdded[0];
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5px;");
        alert.showAndWait();
    }
}