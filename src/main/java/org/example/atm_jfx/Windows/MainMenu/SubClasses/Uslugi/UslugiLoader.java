package org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.atm_jfx.Windows.MainMenu.MainMenu;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.ServiceLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UslugiLoader implements ServiceLoader {
    private final Scene scene;
    private final String cardNumber;
    private final double amount;
    private final String accountNumber;
    private final String serviceName;
    private Scene previousScene;

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
        timeline.setOnFinished(event -> {
            boolean success = logTransaction() && updateBalance();

            if (success) {
                animateText(statusLabel, Duration.seconds(2), () -> {
                    PauseTransition pause = new PauseTransition(Duration.seconds(3));
                    pause.setOnFinished(e -> {
                        UslugiCheck uslugiCheck = new UslugiCheck(primaryStage, cardNumber, amount, accountNumber, serviceName, null);
                        primaryStage.setScene(uslugiCheck.getScene());
                    });
                    pause.play();
                });
            } else {
                statusLabel.setText("Ошибка транзакции");
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e -> {
                    MainMenu mainMenu = new MainMenu(primaryStage, null, accountNumber);
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
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    public boolean logTransaction() {
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String user = "SYSDBA";
        String password = "010802";

        String sql = "INSERT INTO CLIENT_OPERATION (CARD_NUM, OPERATION, COMMENT, DDATE_STAMP) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber); // Номер карты
            pstmt.setString(2, "Оплата Услуги"); // Тип операции
            pstmt.setString(3, "Оплата услуги: " + serviceName + ", Сумма: " + amount); // Комментарий
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); // Дата и время операции

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalance() {
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String user = "SYSDBA";
        String password = "010802";

        String sql = "UPDATE BALANCE_CARD SET BALANCE = BALANCE - ? WHERE FK_CARD = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, cardNumber);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void animateText(Label label, Duration duration, Runnable onFinished) {
        Timeline timeline = new Timeline();
        for (int i = 0; i <= "Транзакция выполняется".length(); i++) {
            String substring = "Транзакция выполняется".substring(0, i);
            KeyFrame keyFrame = new KeyFrame(duration.multiply((double) i / "Транзакция выполняется".length()), e -> label.setText(substring));
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
    }

    public Scene getScene() {
        return scene;
    }
}