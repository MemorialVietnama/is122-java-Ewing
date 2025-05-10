package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.ChangePinMenuInterface;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ChangePinMenu implements ChangePinMenuInterface {
    private final Scene scene;
    private TextField activeField;
    private Label errorLabel;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public ChangePinMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.scene = createScene(primaryStage, previousScene, cardNumber);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    @Override
    public Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(-50);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-background-color: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setTranslateY(50);

        Label titleLabel = new Label("Смена PIN-кода");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        TextField oldPinField = new TextField();
        oldPinField.setStyle("-fx-font-size: 24px; -fx-padding: 15px; -fx-border-color: white; -fx-border-width: 2px; -fx-text-fill: black; -fx-alignment: center; -fx-font-family: 'Arial';");
        oldPinField.setMaxWidth(400);
        oldPinField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if (change.getControlNewText().length() <= 4) {
                return change;
            }
            return null;
        }));

        TextField newPinField = new TextField();
        newPinField.setMaxWidth(400);
        newPinField.setStyle("-fx-font-size: 24px; -fx-padding: 15px; -fx-border-color: white; -fx-border-width: 2px; -fx-text-fill: black; -fx-alignment: center; -fx-font-family: 'Arial';");
        newPinField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if (change.getControlNewText().length() <= 4) {
                return change;
            }
            return null;
        }));

        TextField confirmPinField = new TextField();
        confirmPinField.setMaxWidth(400);
        confirmPinField.setStyle("-fx-font-size: 24px; -fx-padding: 15px; -fx-border-color: white; -fx-border-width: 2px; -fx-text-fill: black; -fx-alignment: center; -fx-font-family: 'Arial';");
        confirmPinField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, change -> {
            if (change.getControlNewText().length() <= 4) {
                return change;
            }
            return null;
        }));

        oldPinField.focusedProperty().addListener((_, _, newVal) -> {
            if (newVal) activeField = oldPinField;
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });
        newPinField.focusedProperty().addListener((_, _, newVal) -> {
            if (newVal) activeField = newPinField;
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });
        confirmPinField.focusedProperty().addListener((_, _, newVal) -> {
            if (newVal) activeField = confirmPinField;
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });

        GridPane numpad = createNumpad();

        Button changePinButton = new Button("Сменить PIN");
        changePinButton.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-background-color: white; -fx-cursor: hand;");
        changePinButton.setOnAction(_ -> {
            String oldPin = oldPinField.getText();
            String newPin = newPinField.getText();
            String confirmPin = confirmPinField.getText();

            if (oldPin.length() != 4 || newPin.length() != 4 || confirmPin.length() != 4) {
                showError("Ошибка: PIN-код должен состоять из 4 цифр");
                return;
            }

            if (!newPin.equals(confirmPin)) {
                showError("Ошибка: Новый PIN и подтверждение не совпадают");
                return;
            }

            if (updatePinInDatabase(cardNumber, oldPin, newPin)) {
                ChangePINCheck changePINCheck = new ChangePINCheck(primaryStage, previousScene, cardNumber) {
                };
                primaryStage.setScene(changePINCheck.getScene());
            } else {
                showError("Ошибка: Не удалось изменить PIN-код");
            }
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
        });

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-text-fill: red; -fx-font-size: 24px; -fx-background-color: white; -fx-cursor: hand;");
        backButton.setOnAction(_ -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            primaryStage.setScene(previousScene);
        });

        root.getChildren().addAll(
                errorLabel,
                titleLabel,
                oldPinField,
                newPinField,
                confirmPinField,
                numpad,
                changePinButton,
                backButton
        );
        root.setTranslateY(-70);
        return new Scene(root, 1920, 1080);
    }

    @Override
    public void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), _ -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), errorLabel);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setOnFinished(_ -> errorLabel.setVisible(false));
            fadeTransition.play();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    @Override
    public GridPane createNumpad() {
        GridPane numpad = new GridPane();
        numpad.setAlignment(Pos.CENTER);
        numpad.setHgap(5);
        numpad.setVgap(5);

        String[] buttons = {
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "←", "0"
        };

        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(buttons[i]);
            button.setStyle("-fx-font-family: 'Arial Black'; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 26px; -fx-padding: 20px; -fx-min-width: 90px; -fx-min-height: 90px; -fx-background-color: red; -fx-border-color: white; -fx-border-width: 2px; -fx-cursor: hand;");
            button.setOnMousePressed(_ -> button.setStyle("-fx-font-family: 'Arial Black'; -fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 26px; -fx-padding: 20px; -fx-min-width: 90px; -fx-min-height: 90px; -fx-background-color: white; -fx-border-color: white; -fx-border-width: 2px; -fx-cursor: hand;"));
            button.setOnMouseReleased(_ -> button.setStyle("-fx-font-family: 'Arial Black'; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 26px; -fx-padding: 20px; -fx-min-width: 90px; -fx-min-height: 90px; -fx-background-color: red; -fx-border-color: white; -fx-border-width: 2px; -fx-cursor: hand;"));

            button.setOnAction(_ -> {
                if (activeField != null) {
                    String buttonText = button.getText();
                    if (buttonText.equals("←")) {
                        String currentText = activeField.getText();
                        if (!currentText.isEmpty()) {
                            activeField.setText(currentText.substring(0, currentText.length() - 1));
                        }
                    } else {
                        activeField.setText(activeField.getText() + buttonText);
                    }
                }
                sessionWarning.checkInactivity(); // Сбрасываем таймер
            });

            numpad.add(button, i % 3, i / 3);
        }

        return numpad;
    }

    @Override
    public boolean updatePinInDatabase(String cardNumber, String oldPin, String newPin) {
        String DATABASE_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb";
        String JDBC_URL = "jdbc:firebirdsql:localhost/3050:" + new File(DATABASE_PATH).getAbsolutePath();
        String USER = "SYSDBA";
        String PASSWORD = "010802";

        String query = "UPDATE CLIENT_CARD SET PIN_CODE = ? WHERE NUMBER_CARD = ? AND PIN_CODE = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPin);
            stmt.setString(2, cardNumber);
            stmt.setString(3, oldPin);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}