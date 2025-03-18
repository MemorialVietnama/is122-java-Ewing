package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi;

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
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.SerialFormInterface;

public class SerialForm implements SerialFormInterface {
    private final Scene scene;
    private final String title;
    private final String accountLabelText;
    private final Runnable transactionHandler;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public SerialForm(Stage primaryStage, String cardNumber, double balance, Scene previousScene, String title, String amountLabelText, String accountLabelText, Runnable transactionHandler) {
        this.title = title;
        this.accountLabelText = accountLabelText;
        this.transactionHandler = transactionHandler;
        this.scene = createScene(primaryStage, previousScene, cardNumber, balance);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: red;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(-100);

        Label titleLabel = new Label(title);
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
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            primaryStage.setScene(previousScene);
        });

        TextField accountField = new TextField();
        accountField.setPromptText(accountLabelText);
        accountField.setStyle("-fx-font-size: 20px; -fx-padding: 10px; -fx-font-family: 'Arial'; -fx-alignment: center;");
        accountField.setMaxWidth(600);
        accountField.setAlignment(Pos.CENTER);
        accountField.setMinWidth(600);

        accountField.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().length() > 30) {
                return null;
            }
            return change; //
        }));

        accountField.textProperty().addListener((observable, oldValue, newValue) -> {
            String sanitizedValue = sanitizeInput(newValue);
            if (!newValue.equals(sanitizedValue)) {
                accountField.setText(sanitizedValue);
                accountField.positionCaret(sanitizedValue.length()); // Установить курсор в конец
            }
            sessionWarning.checkInactivity(); // Сбрасываем таймер при изменении текста
        });

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-font-family: 'Arial Black'; -fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        CustomKeyboard customKeyboard = new CustomKeyboard(accountField);
        VBox keyboardLetter = customKeyboard.createLetterKeyboard();
        keyboardLetter.setAlignment(Pos.CENTER);
        keyboardLetter.setTranslateY(10);
        VBox keyboardDigital = customKeyboard.createDigitKeyboard();
        keyboardDigital.setAlignment(Pos.CENTER);
        keyboardDigital.setTranslateY(0);

        Button confirmButton = new Button("Подтвердить");
        confirmButton.setAlignment(Pos.CENTER);
        confirmButton.setTranslateY(10);
        confirmButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 30px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                """);
        confirmButton.setOnAction(event -> {
            String accountText = accountField.getText();

            // Валидация данных
            if (accountText.isEmpty()) {
                errorLabel.setText("Поле не может быть пустым!");
                return;
            }

            if (!isValidAccount(accountText)) {
                errorLabel.setText("Недопустимые символы в поле!");
                return;
            }

            // Если валидация прошла успешно
            errorLabel.setText(""); // Очистить сообщение об ошибке

            if (transactionHandler != null) {
                transactionHandler.run();
            }

            SumDeposit sumDeposit = new SumDeposit(primaryStage, previousScene, cardNumber, title, balance);
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            primaryStage.setScene(sumDeposit.getScene());

        });

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        HBox headerBox = new HBox(20);
        headerBox.getChildren().addAll(backButton, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setTranslateY(-50);

        VBox mainPageBox = new VBox(3);
        mainPageBox.setAlignment(Pos.CENTER);
        mainPageBox.setStyle("-fx-background-color: red;");
        mainPageBox.getChildren().addAll(infoPanel, headerBox, accountField, confirmButton, errorLabel, gridPane, keyboardDigital, keyboardLetter);
        root.getChildren().addAll(mainPageBox);
        root.setTranslateX(-400);
        root.setTranslateY(-290);
        return new Scene(root, 1920, 1080);
    }

    @Override
    public String sanitizeInput(String input) {
        return input.replaceAll("[<>\"'%;()&]", "");
    }

    public boolean isValidAccount(String input) {
        return input.matches("[a-zA-Z0-9]+");
    }

    public Scene getScene() {
        return scene;
    }
}