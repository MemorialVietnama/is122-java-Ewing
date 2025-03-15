package org.example.atm_jfx.Windows.AuthKeypadCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_jfx.Functions.InfoPanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthWithNumberCard {
    private static final int MAX_CARD_LENGTH = 16;
    private final Scene scene;
    private final Stage primaryStage;

    public AuthWithNumberCard(Stage primaryStage, Scene previousScene) {
        this.primaryStage = primaryStage;

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setTranslateY(-10);

        Label authLabel = createLabel();
        TextField cardNumberField = createCardNumberField();
        Button backButton = createButton(() -> primaryStage.setScene(previousScene));

        // Создаем Label для сообщения об ошибке
        Label errorLabel = createErrorLabel();
        errorLabel.setVisible(false); // Скрываем по умолчанию

        GridPane keyboard = createKeypad(cardNumberField, errorLabel);
        VBox centerBox = new VBox(20, infoPanel, authLabel, cardNumberField, errorLabel, keyboard, backButton);
        centerBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(centerBox);
        root.setStyle("-fx-background: red;");

        scene = new Scene(root, 1980, 1020);
    }

    private Label createLabel() {
        Label label = new Label("Вход по номеру карты");
        label.setStyle(
                "-fx-font-family: 'Arial';" +
                        "-fx-font-size: " + 48 + "px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        return label;
    }

    private Label createErrorLabel() {
        Label label = new Label("Такой карты не существует");
        label.setStyle(
                "-fx-font-family: 'Arial';" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        return label;
    }
    private TextField createCardNumberField() {
        TextField field = new TextField();
        field.setPromptText("Введите номер карты");
        field.setPrefWidth(500);
        field.setMaxWidth(500);
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > MAX_CARD_LENGTH) {
                field.setText(oldValue);
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
                        "-fx-text-security: disc;"+
                        "-fx-alignment: center;" +
                        "-fx-font-weight: bold;"
        );
        return field;
    }

    private Button createButton(Runnable action) {
        Button button = new Button("Назад");
        button.setStyle(
                "-fx-font-size: " + 48 + "px;" +
                        "-fx-font-family: 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: red;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-min-width: " + (48 == 48 ? "150px" : "60px") + ";" +
                        "-fx-min-height: " + (48 == 48 ? "50px" : "60px") + ";"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);
        button.setOnAction(event -> action.run());
        return button;
    }


    private boolean checkCardInDatabase(String cardNumber) {
        // Подключение к базе данных FireBird
        String url = "jdbc:firebirdsql:localhost/3050:C:/ATMV_MODEL_DBASE";
        String user = "SYSDBA";
        String password = "010802";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT COUNT(*) FROM CLIENT_CARD WHERE NUMBER_CARD = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, cardNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Если карта не найдена или произошла ошибка
    }
    private GridPane createKeypad(TextField cardNumberField, Label errorLabel) {
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

                // Handle button events
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
                            """
                    );
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
                        cardNumberField.clear();
                        errorLabel.setVisible(false); // Скрываем сообщение об ошибке при очистке поля
                    });
                } else if (key.equals("->")) {
                    button.setOnAction(event -> {
                        String cardNumber = cardNumberField.getText();
                        if (checkCardInDatabase(cardNumber)) {
                            // Переход к следующему окну, например, вводу пин-кода
                            primaryStage.setScene(new PinCode(primaryStage, this.scene, cardNumber).getScene());
                        } else {
                            errorLabel.setVisible(true); // Показываем сообщение об ошибке
                        }
                    });
                } else {
                    button.setOnAction(event -> {
                        String currentText = cardNumberField.getText();
                        if (currentText.length() < MAX_CARD_LENGTH) {
                            cardNumberField.setText(currentText + key);
                        }
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