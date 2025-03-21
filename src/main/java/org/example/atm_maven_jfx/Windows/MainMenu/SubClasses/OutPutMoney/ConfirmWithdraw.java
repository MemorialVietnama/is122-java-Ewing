package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Loaders.WithdrawLoader;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.Interfaces.ConfirmWithdrawInterface;

public class ConfirmWithdraw implements ConfirmWithdrawInterface {
    private final Scene scene;
    private final String cardNumber;
    private final int amount;
    private SessionWarning sessionWarning; // Поле для SessionWarning

    public ConfirmWithdraw(Stage primaryStage, Scene previousScene, String cardNumber, int amount) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.scene = createScene(primaryStage, previousScene);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label titleLabel = new Label("Выберите способ выдачи");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Button largeBillsButton = new Button("Выдать Крупными");
        largeBillsButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 20px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);

        Button smallBillsButton = new Button("Выдать Мелкими");
        smallBillsButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 20px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);

        // Обработчик кнопки "Выдать Крупными"
        largeBillsButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            WithdrawLoader withdrawLoader = new WithdrawLoader(primaryStage, scene, cardNumber, amount, true);
            primaryStage.setScene(withdrawLoader.getScene());
        });

        // Обработчик кнопки "Выдать Мелкими"
        smallBillsButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            WithdrawLoader withdrawLoader = new WithdrawLoader(primaryStage, scene, cardNumber, amount, false);
            primaryStage.setScene(withdrawLoader.getScene());
        });

        root.getChildren().addAll(titleLabel, largeBillsButton, smallBillsButton);
        root.setTranslateX(-400);
        root.setTranslateY(-400);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }
}