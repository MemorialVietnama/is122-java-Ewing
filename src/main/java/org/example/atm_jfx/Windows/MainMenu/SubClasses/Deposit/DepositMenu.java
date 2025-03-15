package org.example.atm_jfx.Windows.MainMenu.SubClasses.Deposit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_jfx.Functions.InfoPanel;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Deposit.Interfaces.DepositMenuInterface;

public class DepositMenu implements DepositMenuInterface {
    private final Scene scene;
    private TextField amountField;
    private Label errorLabel;

    public DepositMenu(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        this.scene = createScene(primaryStage, previousScene, cardNumber, balance);
    }

    @Override
    public Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setTranslateY(-50);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(30);

        Label titleLabel = new Label("Пополнение наличных");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-font-family: 'Arial Black';");

        Label cardNumberLabel = new Label("Карта: " + cardNumber);
        cardNumberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial");

        Label balanceLabel = new Label("Баланс: " + String.format("%.2f", balance) + " руб.");
        balanceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial;");

        amountField = new TextField();
        amountField.setPromptText("Введите сумму пополнения");
        amountField.setStyle("""
            -fx-font-size: 28px;
            -fx-padding: 15px;
            -fx-border-color: white;
            -fx-border-width: 2px;
            -fx-text-fill: black;
            -fx-alignment: center;
            -fx-font-family: 'Arial';
        """);
        amountField.setMaxWidth(400);
        amountField.setEditable(true);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-font-family: 'Arial Black'; -fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

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
            -fx-font-family: 'Arial Black';
        """);
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));
        root.setTranslateX(-400);
        root.setTranslateY(-300);
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

                button.setOnMousePressed(event -> {
                    button.setStyle("""
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
                    """);
                });

                button.setOnMouseReleased(event -> {
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
                });

                if (key.equals("C")) {
                    button.setOnAction(event -> {
                        String text = amountField.getText();
                        if (!text.isEmpty()) {
                            amountField.setText(text.substring(0, text.length() - 1));
                        }
                    });
                } else if (key.equals("->")) {
                    button.setOnAction(event -> processDeposit(primaryStage, cardNumber));
                } else {
                    button.setOnAction(event -> handleNumberInput(key));
                }

                keypad.add(button, col, row);
                index++;
            }
        }

        return keypad;
    }

    @Override
    public void handleNumberInput(String digit) {
        String currentText = amountField.getText();
        if (currentText.length() < 5) {
            try {
                int amount = Integer.parseInt(currentText + digit);
                if (amount <= 15000) {
                    amountField.setText(currentText + digit);
                    errorLabel.setText("");
                } else {
                    errorLabel.setText("Максимальная сумма ввода: 15000 руб.");
                }
            } catch (NumberFormatException e) {
                errorLabel.setText("Неверный ввод!");
            }
        }
    }

    @Override
    public void processDeposit(Stage primaryStage, String cardNumber) {
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

        System.out.println("Заявка на пополнение : " + amount + " руб.");
        amountField.clear();
        errorLabel.setText("Операция успешна!");

        // Proceed to DepositOperation
        try {
            DepositOperation depositOperation = new DepositOperation(primaryStage, scene, cardNumber, amount);
            primaryStage.setScene(depositOperation.getScene());
        } catch (Exception e) {
            errorLabel.setText("Ошибка при переходе к следующей сцене!");
            e.printStackTrace();
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}
