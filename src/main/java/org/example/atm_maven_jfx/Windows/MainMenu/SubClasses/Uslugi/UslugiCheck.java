package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.SessionWarning; // Импортируем SessionWarning
import org.example.atm_maven_jfx.Windows.MainMenu.MainMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.ServiceCheck;

public class UslugiCheck implements ServiceCheck {
    private final Scene scene;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public UslugiCheck(Stage primaryStage, String cardNumber, double amount, String accountNumber, String serviceName) {
        this.scene = createScene(primaryStage, cardNumber, amount, accountNumber, serviceName);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, String cardNumber, double amount, String accountNumber, String serviceName) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: green;");

        Label successLabel = new Label("Услуга Успешна");
        successLabel.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-family: Arial;");

        Label cardNumberLabel = new Label("Номер карты: " + cardNumber);
        cardNumberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial;");

        Label amountLabel = new Label("Сумма оплаты: " + amount + " кредитов");
        amountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial;");

        Label accountNumberLabel = new Label("Номер счета: " + accountNumber);
        accountNumberLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial;");

        Label serviceNameLabel = new Label("Название услуги: " + serviceName);
        serviceNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-family: Arial;");

        Button backButton = new Button("На главную");
        backButton.setStyle("""
                    -fx-text-fill: green;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 30px;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                """);
        backButton.setOnAction(_ -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            MainMenu mainMenu = new MainMenu(primaryStage, cardNumber);
            primaryStage.setScene(mainMenu.getScene());
            primaryStage.show();
        });

        root.getChildren().addAll(successLabel, cardNumberLabel, amountLabel, accountNumberLabel, serviceNameLabel, backButton);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }
}