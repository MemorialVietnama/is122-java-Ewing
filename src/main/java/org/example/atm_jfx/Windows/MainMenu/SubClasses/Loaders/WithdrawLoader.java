package org.example.atm_jfx.Windows.MainMenu.SubClasses.Loaders;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.atm_jfx.Windows.MainMenu.MainMenu;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Loaders.Interfaces.WithdrawLoaderInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WithdrawLoader implements WithdrawLoaderInterface {
    private final Scene scene;
    private Text message;
    private List<String> messages;
    private String currentMessage;
    private final Stage primaryStage;

    public WithdrawLoader(Stage primaryStage, Scene mainMenuScene, String cardNumber, int amount) {
        this.primaryStage = primaryStage;
        this.scene = createScene(cardNumber, amount);
        updateBalance(cardNumber, amount);
    }

    private Scene createScene(String cardNumber, double withdrawalAmount) {
        logTransactionDetails(cardNumber, withdrawalAmount);
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");
        message = new Text();
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Arial", 50));
        message.setStyle("-fx-font-weight: bold;");
        root.getChildren().add(message);
        messages = Arrays.asList(
                "Мы точно не воруем деньги...",
                "Это не мы...",
                "Еще чуть-чуть...",
                "Услышал тебя родной...",
                "Блокирую карту..."
        );
        createTextAnimation();
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    private void logTransactionDetails(String cardNumber, double withdrawalAmount) {
        System.out.println("Карта: " + cardNumber);
        System.out.println("Сумма снятия: " + withdrawalAmount);
    }

    private void createTextAnimation() {
        currentMessage = getRandomMessage();
        Timeline timeline = new Timeline();
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0), event -> animateText(currentMessage, true));
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(2), event -> animateText(currentMessage, false));
        KeyFrame keyFrame3 = new KeyFrame(Duration.seconds(4), event -> switchMessage());
        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void animateText(String messageText, boolean isTyping) {
        StringBuilder currentText = new StringBuilder(messageText);
        if (isTyping) {
            message.setText("");
            new Thread(() -> {
                for (int i = 0; i < currentText.length(); i++) {
                    final int index = i;
                    try {
                        Thread.sleep(100);
                        Platform.runLater(() -> message.setText(currentText.substring(0, index + 1)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            new Thread(() -> {
                for (int i = currentText.length(); i >= 0; i--) {
                    final int index = i;
                    try {
                        Thread.sleep(100);
                        Platform.runLater(() -> message.setText(currentText.substring(0, index)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void switchMessage() {
        currentMessage = getRandomMessage();
    }

    private String getRandomMessage() {
        Random random = new Random();
        return messages.get(random.nextInt(messages.size()));
    }

    public Scene getScene() {
        return scene;
    }

    private void updateBalance(String cardNumber, double withdrawalAmount) {
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String username = "SYSDBA";
        String password = "010802";
        String sql = "UPDATE BALANCE_CARD SET BALANCE = BALANCE - ? WHERE FK_CARD = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, withdrawalAmount);
            pstmt.setString(2, cardNumber);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Баланс успешно обновлен.");
                scheduleSceneChange(cardNumber);
            } else {
                System.out.println("Ошибка: Карта не найдена или недостаточно средств.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка при работе с базой данных.");
        }
    }

    public void scheduleSceneChange(String cardNumber) {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(10), event -> {
            Platform.runLater(() -> {
                Scene previousScene = new Scene(new StackPane(), 1920, 1080);
                MainMenu mainMenu = new MainMenu(primaryStage, previousScene, cardNumber);
                primaryStage.setScene(mainMenu.getScene());
                primaryStage.show();
            });
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
}
