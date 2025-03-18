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

    public static boolean display(TableView<Incossations.CashStorage> tableView) {
        boolean[] isDataAdded = {false};
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Добавить деньги");

        // Основной контейнер
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #f4f4f9; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        // Поле для ввода суммы
        Label amountLabel = new Label("Введите сумму:");
        amountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");
        amountField.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-color: #aaa; -fx-border-radius: 3px;");

        // Слайдеры для выбора номиналов
        Label sliderLabel = new Label("Выберите соотношение номиналов:");
        sliderLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Slider slider50 = createSlider();
        Slider slider100 = createSlider();
        Slider slider500 = createSlider();
        Slider slider1000 = createSlider();
        Slider slider2000 = createSlider();
        Slider slider5000 = createSlider();

        slider50.setDisable(true);
        slider100.setDisable(true);
        slider500.setDisable(true);
        slider1000.setDisable(true);
        slider2000.setDisable(true);
        slider5000.setDisable(true);

        // Обработчик изменения текста в поле суммы
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                slider50.setDisable(false);
                slider100.setDisable(false);
                slider500.setDisable(false);
                slider1000.setDisable(false);
                slider2000.setDisable(false);
                slider5000.setDisable(false);
            } else {
                slider50.setDisable(true);
                slider100.setDisable(true);
                slider500.setDisable(true);
                slider1000.setDisable(true);
                slider2000.setDisable(true);
                slider5000.setDisable(true);
            }
        });

        // Кнопка "ОК"
        Button okButton = new Button("ОК");
        okButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-color: #4caf50; -fx-text-fill: white; -fx-border-radius: 5px;");
        okButton.setOnAction(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                if (amount <= 0) {
                    showAlert("Сумма должна быть больше нуля.");
                    return;
                }
                double totalSliderValue = slider50.getValue() + slider100.getValue() + slider500.getValue() +
                        slider1000.getValue() + slider2000.getValue() + slider5000.getValue();

                if (totalSliderValue == 0) {
                    showAlert("Выберите соотношение номиналов.");
                    return;
                }

                int count50 = (int) ((slider50.getValue() / totalSliderValue) * amount / 50);
                int count100 = (int) ((slider100.getValue() / totalSliderValue) * amount / 100);
                int count500 = (int) ((slider500.getValue() / totalSliderValue) * amount / 500);
                int count1000 = (int) ((slider1000.getValue() / totalSliderValue) * amount / 1000);
                int count2000 = (int) ((slider2000.getValue() / totalSliderValue) * amount / 2000);
                int count5000 = (int) ((slider5000.getValue() / totalSliderValue) * amount / 5000);

                // Вставка данных в базу
                DatabaseService.insertCashIntoDatabase(count50, 50);
                DatabaseService.insertCashIntoDatabase(count100, 100);
                DatabaseService.insertCashIntoDatabase(count500, 500);
                DatabaseService.insertCashIntoDatabase(count1000, 1000);
                DatabaseService.insertCashIntoDatabase(count2000, 2000);
                DatabaseService.insertCashIntoDatabase(count5000, 5000);

                isDataAdded[0] = true;

                window.close();

                Stage primaryStage = (Stage) tableView.getScene().getWindow();
                Scene loadMoneyScene = LoadMoney.createScene(primaryStage, LoadingLoadMoney.createScene(primaryStage, tableView.getScene()));
                primaryStage.setScene(loadMoneyScene);

            } catch (NumberFormatException ex) {
                showAlert("Введите корректную сумму.");
            } catch (SQLException ex) {
                showAlert("Ошибка при добавлении данных в базу.");
                ex.printStackTrace();
            }
        });

        // Добавление элементов в контейнер
        layout.getChildren().addAll(
                amountLabel, amountField, sliderLabel,
                new HBox(10, new Label("50"), slider50),
                new HBox(10, new Label("100"), slider100),
                new HBox(10, new Label("500"), slider500),
                new HBox(10, new Label("1000"), slider1000),
                new HBox(10, new Label("2000"), slider2000),
                new HBox(10, new Label("5000"), slider5000),
                okButton
        );

        Scene scene = new Scene(layout, 500, 500);
        window.setScene(scene);
        window.showAndWait();

        return isDataAdded[0];
    }

    // Создание слайдера
    private static Slider createSlider() {
        Slider slider = new Slider(0, 100, 0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(5);
        slider.setBlockIncrement(10);
        slider.setStyle("-fx-control-inner-background: red; -fx-font-size: 12px;");
        return slider;
    }

    // Показ сообщения об ошибке
    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5px;");
        alert.showAndWait();
    }
}