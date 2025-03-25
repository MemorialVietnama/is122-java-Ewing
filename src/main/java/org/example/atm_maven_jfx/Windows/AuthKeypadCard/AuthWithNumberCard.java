    package org.example.atm_maven_jfx.Windows.AuthKeypadCard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning;

import java.sql.SQLException;

public class AuthWithNumberCard {
    private static final int MAX_CARD_LENGTH = 16;
    private final Scene scene;
    private final Stage primaryStage;
    private final SessionWarning sessionWarning;

    public AuthWithNumberCard(Stage primaryStage, Scene previousScene) {
        this.primaryStage = primaryStage;

        sessionWarning = new SessionWarning(primaryStage);
        sessionWarning.checkInactivity();

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setTranslateY(-10);

        Label authLabel = createLabel();
        TextField cardNumberField = createCardNumberField();
        Button backButton = createButton(previousScene);

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
                        "-fx-font-size: 48px;" +
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
        field.textProperty().addListener((_, oldValue, newValue) -> {
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
                        "-fx-text-security: disc;" +
                        "-fx-alignment: center;" +
                        "-fx-font-weight: bold;"
        );
        field.setOnKeyTyped(_ -> sessionWarning.checkInactivity());
        return field;
    }

    private Button createButton(Scene previousScene) {
        Button button = new Button("Назад");
        button.setStyle(
                "-fx-font-size: 48px;" +
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
            SceneTransition.changeSceneWithAnimation(primaryStage, previousScene);
            sessionWarning.stopInactivityCheck(); // Остановить таймер вместо сброса
        });
        return button;
    }

    private GridPane createKeypad(TextField cardNumberField, Label errorLabel) {
        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setPadding(new Insets(10));
        keypad.setAlignment(Pos.CENTER);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "->"};
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
                        cardNumberField.clear();
                        errorLabel.setVisible(false); // Скрываем сообщение об ошибке при очистке поля
                        sessionWarning.checkInactivity(); // Сбрасываем таймер
                    });
                } else if (key.equals("->")) {
                    button.setOnAction(_ -> {
                        String cardNumber = cardNumberField.getText();
                        try {
                            boolean cardExists = DatabaseService.checkCardInDatabase(cardNumber);
                            if (cardExists) {
                                sessionWarning.stopInactivityCheck(); // Остановить таймер перед переходом на PinCode
                                SceneTransition.changeSceneWithAnimation(primaryStage, new PinCode(primaryStage, this.scene, cardNumber).getScene());
                            } else {
                                errorLabel.setVisible(true);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            errorLabel.setVisible(true);
                        }
                    });
                } else {
                    button.setOnAction(_ -> {
                        String currentText = cardNumberField.getText();
                        if (currentText.length() < MAX_CARD_LENGTH) {
                            cardNumberField.setText(currentText + key);
                        }
                        sessionWarning.checkInactivity();
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