package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Loaders;

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
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.MoneyWithdrawalScene;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WithdrawLoader {
    private final Scene scene;
    private final Stage primaryStage;
    private final String cardNumber;
    private final int amount;
    private final boolean isLargeBills;
    private Text message;
    private List<String> messages;
    private String currentMessage;

    public WithdrawLoader(Stage primaryStage, Scene mainMenuScene, String cardNumber, int amount, boolean isLargeBills) {
        this.primaryStage = primaryStage;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.isLargeBills = isLargeBills;
        this.scene = createScene(cardNumber, amount);
    }

    private Scene createScene(String cardNumber, double withdrawalAmount) {
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
        root.setTranslateY(-400);
        return new Scene(root, 1920, 1080);
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

        Timeline sceneTransition = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            MoneyWithdrawalScene moneyWithdrawalScene = new MoneyWithdrawalScene(primaryStage, scene, cardNumber, amount, isLargeBills);
            primaryStage.setScene(moneyWithdrawalScene.getScene());
        }));
        sceneTransition.play();
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
}
