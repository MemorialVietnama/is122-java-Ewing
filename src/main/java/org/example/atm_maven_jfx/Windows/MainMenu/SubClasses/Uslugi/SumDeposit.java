package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SessionWarning; // Импортируем SessionWarning

public class SumDeposit {
    private final String tittle;
    private final Scene scene;
    private final String cardNumber;
    private final double balance;
    private final Scene previousScene;
    private Scene uslugiLoaderScene;
    private Runnable transactionHandler;
    private TextField amountField;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public SumDeposit(Stage primaryStage, Scene previousScene, String cardNumber, String tittle, double balance) {
        this.cardNumber = cardNumber;
        this.tittle = tittle;
        this.balance = balance;
        this.previousScene = previousScene;
        this.scene = createScene(primaryStage);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: red;");

        System.out.println("Sum Deposit : cardNumber: " + cardNumber);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(-80);

        Label titleLabel = new Label("Введите сумму оплаты");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 30px;");

        Button backButton = new Button("Назад");
        backButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 30px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                """);
        backButton.setOnAction(event -> {
            primaryStage.setScene(previousScene);
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });

        amountField = new TextField(); // Инициализируем поле ввода
        amountField.setPromptText("Сумма оплаты");
        amountField.setStyle("-fx-font-size: 20px; -fx-padding: 10px; -fx-font-family: 'Arial';");
        amountField.setMaxWidth(600);
        amountField.setMinWidth(600);

        // Ограничение ввода только цифр и одной точки для десятичных чисел
        amountField.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) {
                return change; // Принять изменение
            }
            return null; // Отклонить изменение
        }));

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-family: 'Arial Black'; -fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        GridPane keypad = createKeypad(); // Создаем клавиатуру
        // Кнопка для подтверждения суммы
        Button confirmButton = new Button("Подтвердить");
        confirmButton.setAlignment(Pos.CENTER);
        confirmButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 30px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                """);
        confirmButton.setOnAction(event -> {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                errorLabel.setText("Введите сумму!");
                return;
            }

            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                errorLabel.setText("Сумма должна быть больше нуля!");
                return;
            }

            if (amount > balance) {
                errorLabel.setText("Недостаточно средств на счете!");
                return;
            }

            // Вызов обработчика транзакции
            if (transactionHandler != null) {
                transactionHandler.run();
            }

            // Создаем экземпляр UslugiLoader и передаем необходимые данные
            UslugiLoader uslugiLoader = new UslugiLoader(primaryStage, cardNumber, amount, amountField.getText(), tittle);
            primaryStage.setScene(uslugiLoader.getScene());
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });
        // Сетка для размещения элементов
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        HBox headerBox = new HBox(20);
        headerBox.getChildren().addAll(backButton, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setTranslateY(-50);

        VBox mainPageBox = new VBox(10);
        mainPageBox.setAlignment(Pos.CENTER);
        mainPageBox.setStyle("-fx-background-color: red;");
        mainPageBox.getChildren().addAll(infoPanel, headerBox, amountField, confirmButton, errorLabel, keypad); // Добавляем клавиатуру
        root.getChildren().addAll(mainPageBox);
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    private GridPane createKeypad() {
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setPadding(new Insets(10));
        keypad.setAlignment(Pos.CENTER);

        String[] keys = {
                "1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "C", "0"
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

                // Handle button events
                button.setOnMousePressed(event -> {
                    if (sessionWarning != null) {
                        sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                    }
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
                        if (sessionWarning != null) {
                            sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                        }
                        String text = amountField.getText();
                        if (!text.isEmpty()) {
                            amountField.setText(text.substring(0, text.length() - 1));
                        }
                    });
                } else {
                    button.setOnAction(event -> {
                        if (sessionWarning != null) {
                            sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                        }
                        String currentText = amountField.getText();
                        amountField.setText(currentText + key);
                    });
                }

                keypad.add(button, col, row);
                index++;
            }
        }

        return keypad;
    }

    public Scene getScene() {
        return scene;
    }
}