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
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Windows.MainMenu.MainMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.DepositOperation;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Loaders.Interfaces.DepositLoaderInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DepositLoader implements DepositLoaderInterface {
    private final Scene scene;
    private final Stage primaryStage;

    public DepositLoader(Stage primaryStage, Scene mainMenuScene, String cardNumber, int depositAmount, List<DepositOperation.Denomination> banknotes) {
        this.primaryStage = primaryStage;
        this.scene = createScene(cardNumber, depositAmount);

        // Обновляем баланс через DatabaseService
        boolean isBalanceUpdated = DatabaseService.updateBalance(cardNumber, depositAmount);
        if (isBalanceUpdated) {
            System.out.println("Баланс успешно обновлен.");
        } else {
            System.out.println("Ошибка: Карта не найдена.");
        }

        // Добавляем банкноты в хранилище через DatabaseService
        boolean areBanknotesAdded = DatabaseService.addBanknotesToStorage(banknotes);
        if (areBanknotesAdded) {
            System.out.println("Банкноты успешно добавлены в хранилище.");
        } else {
            System.out.println("Ошибка при добавлении банкнот в хранилище.");
        }

        scheduleSceneChange(cardNumber);
    }

    private Scene createScene(String cardNumber, int depositAmount) {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");
        Text message = new Text();
        message.setFill(Color.WHITE);
        message.setFont(Font.font("Arial", 50));
        message.setStyle("-fx-font-weight: bold;");
        root.getChildren().add(message);
        List<String> messages = Arrays.asList(
                "Пополнение баланса...",
                "Деньги на подходе...",
                "Еще чуть-чуть...",
                "Обрабатываем ваш платеж...",
                "Почти готово..."
        );
        createTextAnimation(message, messages);
        return new Scene(root, 1920, 1080);
    }

    private void createTextAnimation(Text message, List<String> messages) {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0), event -> animateText(message, getRandomMessage(messages), true));
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(2), event -> animateText(message, getRandomMessage(messages), false));
        KeyFrame keyFrame3 = new KeyFrame(Duration.seconds(4), event -> {});
        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void animateText(Text message, String text, boolean isTyping) {
        StringBuilder currentText = new StringBuilder(text);
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

    private String getRandomMessage(List<String> messages) {
        Random random = new Random();
        return messages.get(random.nextInt(messages.size()));
    }

    public Scene getScene() {
        return scene;
    }

    public void scheduleSceneChange(String cardNumber) {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3), event -> {
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