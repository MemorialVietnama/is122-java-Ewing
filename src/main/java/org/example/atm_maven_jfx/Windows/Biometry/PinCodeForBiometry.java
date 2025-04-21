package org.example.atm_maven_jfx.Windows.Biometry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.MainMenu.MainMenu;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PinCodeForBiometry {
    private static final int PIN_LENGTH = 4; // Длина пин-кода
    private static final Logger LOGGER = Logger.getLogger(PinCodeForBiometry.class.getName());
    private final Scene scene;
    private final Stage primaryStage;
    private final String cardNumber; // Номер карты, полученный через биометрию
    private final SessionWarning sessionWarning;

    public PinCodeForBiometry(Stage primaryStage, String cardNumber) {
        this.primaryStage = primaryStage;
        this.cardNumber = cardNumber;

        // Создаем объект SessionWarning для проверки бездействия
        sessionWarning = new SessionWarning(primaryStage);
        sessionWarning.checkInactivity();

        // Создание элементов интерфейса
        Label pinLabel = createLabel("Введите пин-код", 62);
        pinLabel.setStyle("""
               -fx-text-fill: white;
               -fx-font-size: 62;
               """);
        PasswordField pinField = createPinField();
        Button backButton = createButton();

        // Label для отображения ошибок
        Label errorLabel = createLabel("", 24);
        errorLabel.setStyle("-fx-text-fill: red;");

        // Создание клавиатуры
        GridPane keyboard = createKeypad(pinField, errorLabel);

        // Основной контейнер
        VBox centerBox = new VBox(20, pinLabel, pinField, errorLabel, keyboard, backButton);
        centerBox.setAlignment(Pos.CENTER);

        // Корневой контейнер
        StackPane root = new StackPane(centerBox);
        root.setStyle("-fx-background-color: red;");

        // Создание сцены
        scene = new Scene(root, 1920, 1080);
    }

    private Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-family: 'Arial';" +
                        "-fx-font-size: " + fontSize + "px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: black;"
        );
        return label;
    }

    private PasswordField createPinField() {
        PasswordField field = new PasswordField();
        field.setPromptText("Введите пин-код");
        field.setPrefWidth(500);
        field.setMaxWidth(500);
        field.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.length() > PIN_LENGTH) {
                field.setText(oldValue); // Ограничиваем длину пин-кода
            }
        });
        field.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-pref-width: 400px;" +
                        "-fx-pref-height: 50px;" +
                        "-fx-border-color: red;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-text-fill: black;" +
                        "-fx-prompt-text-fill: red;" +
                        "-fx-text-security: disc;" +
                        "-fx-alignment: center;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-box-border: transparent; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent;"
        );
        // Сброс таймера при вводе текста
        field.setOnKeyTyped(_ -> sessionWarning.checkInactivity());
        return field;
    }

    private Button createButton() {
        Button button = new Button("Назад");
        button.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: red;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-min-width: 150px;" +
                        "-fx-min-height: 50px;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

        button.setOnAction(_ -> {
            sessionWarning.stopInactivityCheck(); // Останавливаем проверку бездействия
            primaryStage.setScene(new BioAuthScene(primaryStage, null).createScene());
        });

        return button;
    }

    private GridPane createKeypad(PasswordField pinField, Label errorLabel) {
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
                Button button = createKeypadButton(keys[index], pinField, errorLabel);
                keypad.add(button, col, row);
                index++;
            }
        }

        return keypad;
    }

    private Button createKeypadButton(String key, PasswordField pinField, Label errorLabel) {
        Button button = new Button(key);
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
                """
        ));

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
                """
        ));

        if (key.equals("C")) {
            button.setOnAction(_ -> {
                pinField.clear();
                errorLabel.setVisible(false); // Скрываем сообщение об ошибке при очистке поля
                sessionWarning.checkInactivity(); // Сбрасываем таймер
            });
        } else if (key.equals("->")) {
            button.setOnAction(_ -> handlePinSubmission(pinField.getText(), errorLabel));
        } else {
            button.setOnAction(_ -> {
                String currentText = pinField.getText();
                if (currentText.length() < PIN_LENGTH) {
                    pinField.setText(currentText + key);
                }
            });
        }

        return button;
    }

    private void handlePinSubmission(String pin, Label errorLabel) {
        System.out.println("Введенный пин-код: " + pin); // Вывод введенного пин-кода в консоль
        if (pin.length() == PIN_LENGTH) {
            try {
                // Проверка пин-кода через базу данных
                boolean isValidPin = DatabaseService.checkPinCode(cardNumber, pin);
                if (isValidPin) {
                    // Логирование успешного входа
                    logOperation(cardNumber, "Успешный вход в систему через биометрию");
                    MainMenu mainMenu = new MainMenu(primaryStage, cardNumber);
                    SceneTransition.changeSceneWithAnimation(primaryStage, mainMenu.getScene());
                } else {
                    // Логирование неудачного входа
                    logOperation(cardNumber, "Неверный пин-код после биометрии");
                    errorLabel.setText("Неправильный пин-код."); // Установка текста ошибки
                    errorLabel.setVisible(true); // Делаем Label видимым
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Ошибка при проверке пин-кода", e);
                errorLabel.setText("Ошибка при проверке пин-кода."); // Установка текста ошибки
                errorLabel.setVisible(true); // Делаем Label видимым
            }
        } else {
            errorLabel.setText("Пин-код должен содержать " + PIN_LENGTH + " цифр"); // Установка текста ошибки
            errorLabel.setVisible(true); // Делаем Label видимым
        }
        sessionWarning.checkInactivity(); // Сбрасываем таймер
    }

    private void logOperation(String cardNumber, String comment) {
        DatabaseService.logOperation(cardNumber, "Вход в систему через биометрию", comment);
    }

    public Scene getScene() {
        return scene;
    }
}