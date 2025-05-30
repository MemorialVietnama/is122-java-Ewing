package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Windows.MainMenu.MainMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.ServiceLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UslugiLoader implements ServiceLoader {
    private final Scene scene;
    private final String cardNumber;
    private final double amount;
    private final String accountNumber;
    private final String serviceName;

    public UslugiLoader(Stage primaryStage, String cardNumber, double amount, String accountNumber, String serviceName) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.serviceName = serviceName;
        this.scene = createScene(primaryStage);
    }

    private Scene createScene(Stage primaryStage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: red;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        Circle progressCircle = new Circle(50, Color.GREEN);
        progressCircle.setEffect(shadow);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressCircle.radiusProperty(), 50)),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressCircle.radiusProperty(), 100))
        );
        timeline.setCycleCount(1);
        timeline.setOnFinished(_ -> {
            boolean success = DatabaseService.logTransaction(cardNumber, "Оплата Услуги", "Оплата услуги: " + serviceName + ", Сумма: " + amount);

            if (success) {
                // Прямой SQL-запрос для обновления баланса
                try (Connection connection = DatabaseService.getConnection();
                     PreparedStatement statement = connection.prepareStatement("UPDATE BALANCE_CARD SET BALANCE = BALANCE - ? WHERE FK_CARD = ?")) {

                    statement.setDouble(1, amount); // Вычитаем сумму
                    statement.setString(2, cardNumber); // Номер карты

                    int rowsAffected = statement.executeUpdate();
                    success = rowsAffected > 0; // Проверяем, была ли обновлена хотя бы одна строка
                } catch (SQLException e) {
                    e.printStackTrace();
                    success = false; // Ошибка при выполнении запроса
                }
            }

            if (success) {
                animateText(statusLabel, Duration.seconds(2), () -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(_ -> {
                        UslugiCheck uslugiCheck = new UslugiCheck(primaryStage, cardNumber, amount, accountNumber, serviceName);
                        primaryStage.setScene(uslugiCheck.getScene());
                    });
                    pause.play();
                });
            } else {
                statusLabel.setText("Ошибка транзакции");
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(_ -> {
                    MainMenu mainMenu = new MainMenu(primaryStage, accountNumber);
                    primaryStage.setScene(mainMenu.getScene());
                    primaryStage.show();
                });
                pause.play();
            }
        });
        timeline.play();

        VBox mainPageBox = new VBox(10);
        mainPageBox.setAlignment(Pos.CENTER);
        mainPageBox.setStyle("-fx-background-color: red;");
        mainPageBox.getChildren().addAll(statusLabel, progressCircle);

        root.getChildren().addAll(mainPageBox);
        return new Scene(root, 1920, 1080);
    }

    private void animateText(Label label, Duration duration, Runnable onFinished) {
        Timeline timeline = new Timeline();
        for (int i = 0; i <= "Транзакция выполняется".length(); i++) {
            String substring = "Транзакция выполняется".substring(0, i);
            KeyFrame keyFrame = new KeyFrame(duration.multiply((double) i / "Транзакция выполняется".length()), _ -> label.setText(substring));
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setOnFinished(_ -> onFinished.run());
        timeline.play();
    }

    public Scene getScene() {
        return scene;
    }

}