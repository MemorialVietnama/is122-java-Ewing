package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.ChangePINCheckInterface;

public abstract class ChangePINCheck implements ChangePINCheckInterface {
    private final Scene scene;
    private final String cardNumber;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public ChangePINCheck(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.cardNumber = cardNumber;
        this.scene = createScene(primaryStage, previousScene);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();

        // Логируем операцию через DatabaseService
        DatabaseService.logOperation(cardNumber, "Смена PIN", "Успешная смена PIN-кода для карты " + cardNumber);
    }

    @Override
    public Scene createScene(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label successLabel = new Label("PIN-код успешно изменен!");
        successLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-background-color: white; -fx-cursor: hand;");
        backButton.setOnAction(_ -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            primaryStage.setScene(previousScene);
        });

        root.getChildren().addAll(successLabel, backButton);

        return new Scene(root, 1920, 1080);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}