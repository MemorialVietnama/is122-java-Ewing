package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning;

public class OutputMoney {
    private final Scene scene;
    private final double balance;
    private TextField amountField;
    private Label errorLabel;
    private final SessionWarning sessionWarning;

    public OutputMoney(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        this.balance = balance;
        this.scene = createScene(primaryStage, previousScene, cardNumber);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setTranslateY(-50);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(30);

        Label titleLabel = new Label("Снятие наличных");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Label cardNumberLabel = new Label("Карта: " + cardNumber);
        cardNumberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        Label balanceLabel = new Label("Баланс: " + String.format("%.2f", balance) + " руб.");
        balanceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        amountField = new TextField();
        amountField.setPromptText("Введите сумму снятия");
        amountField.setStyle("""
                    -fx-font-size: 28px;
                    -fx-padding: 15px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-text-fill: black;
                    -fx-alignment: center;
                    -fx-font-family: 'Arial Black';
                """);
        amountField.setMaxWidth(400);
        amountField.setEditable(true);

        // Сброс таймера при вводе текста
        amountField.setOnKeyTyped(_ -> sessionWarning.checkInactivity());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: yellow; -fx-font-size: 20px;");

        GridPane keypad = createKeypad(primaryStage, cardNumber);

        Button backButton = new Button("Назад");
        backButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 20px;
                    -fx-border-color: white;
                    -fx-font-weight: bold;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);
        backButton.setOnAction(_ -> {
            sessionWarning.checkInactivity(); // Сбрасываем таймер
            SceneTransition.changeSceneWithAnimation(primaryStage, previousScene);
        });

        root.getChildren().addAll(infoPanel, titleLabel, cardNumberLabel, balanceLabel, amountField, errorLabel, keypad, backButton);
        return new Scene(root, 1920, 1080);
    }

    private GridPane createKeypad(Stage primaryStage, String cardNumber) {
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setPadding(new Insets(10));
        keypad.setAlignment(Pos.CENTER);

        String[] keys = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "C", "0", "->"
        };

        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                if (index >= keys.length) break;
                Button button = new Button(keys[index]);
                button.setStyle("""
                            -fx-font-family: 'Arial Black';
                            -fx-font-weight: bold;
                            -fx-text-fill: white;
                            -fx-font-size: 28px;
                            -fx-padding: 20px;
                            -fx-min-width: 90px;
                            -fx-min-height: 90px;
                            -fx-background-color: red;
                            -fx-border-color: white;
                            -fx-border-width: 2px;
                            -fx-cursor: hand;
                        """);
                String key = keys[index];

                button.setOnMousePressed(_ -> button.setStyle("""
                        -fx-font-family: 'Arial Black';
                        -fx-font-weight: bold;
                        -fx-text-fill: black;
                        -fx-font-size: 28px;
                        -fx-padding: 20px;
                        -fx-min-width: 90px;
                        -fx-min-height: 90px;
                        -fx-background-color: white;
                        -fx-border-color: white;
                        -fx-border-width: 2px;
                        -fx-cursor: hand;
                        """));

                button.setOnMouseReleased(_ -> button.setStyle("""
                        -fx-font-family: 'Arial Black';
                        -fx-font-weight: bold;
                        -fx-text-fill: white;
                        -fx-font-size: 28px;
                        -fx-padding: 20px;
                        -fx-min-width: 90px;
                        -fx-min-height: 90px;
                        -fx-background-color: red;
                        -fx-border-color: white;
                        -fx-border-width: 2px;
                        -fx-cursor: hand;
                        """));

                if (key.equals("C")) {
                    button.setOnAction(_ -> {
                        String text = amountField.getText();
                        if (!text.isEmpty()) {
                            amountField.setText(text.substring(0, text.length() - 1));
                        }
                        sessionWarning.checkInactivity(); // Сбрасываем таймер
                    });
                } else if (key.equals("->")) {
                    button.setOnAction(_ -> {
                        if (sessionWarning != null) {
                            sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                        }
                        processWithdrawal(primaryStage, cardNumber);
                    });
                } else {
                    button.setOnAction(_ -> {
                        if (sessionWarning != null) {
                            sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                        }
                        handleNumberInput(key);

                    });
                }

                keypad.add(button, col, row);
                index++;
            }
        }

        return keypad;
    }

    private void handleNumberInput(String digit) {
        String currentText = amountField.getText();
        if (currentText.length() < 5) {
            try {
                int amount = Integer.parseInt(currentText + digit);
                if (amount <= 15000) {
                    amountField.setText(currentText + digit);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Максимальная сумма: 15000 руб.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Неверный ввод!");
            }
        }
    }

    private void processWithdrawal(Stage primaryStage, String cardNumber) {
        String text = amountField.getText();
        if (text.isEmpty()) {
            errorLabel.setText("Введите сумму!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            errorLabel.setText("Неверный формат суммы!");
            return;
        }

        if (amount % 50 != 0) {
            errorLabel.setText("Сумма должна быть кратна 50!");
            return;
        }
        if (amount > balance) {
            errorLabel.setText("Недостаточно средств!");
            return;
        }

        WithdrawalProcessor withdrawal = new WithdrawalProcessor(primaryStage, cardNumber, amount);
        primaryStage.setScene(withdrawal.getScene());
    }

    public Scene getScene() {
        return scene;
    }
}