package org.example.atm_maven_jfx.Functions;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;

public class SessionWarning {

    private final Stage primaryStage;
    private Timeline inactivityTimeline; // Таймер бездействия
    private Timeline countdownTimeline; // Таймер обратного отсчета
    private int countdown = 10; // Начальное значение таймера
    private boolean isRunning = false; // Флаг для проверки, запущен ли таймер

    public SessionWarning(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Запускает проверку бездействия пользователя.
     */
    public void checkInactivity() {
        if (!isRunning) {
            isRunning = true;
            System.out.println("Таймер бездействия запущен");

            // Создаем таймер бездействия
            inactivityTimeline = new Timeline(new KeyFrame(Duration.seconds(20), event -> {
                showWarning(); // Показываем предупреждение после 20 секунд бездействия
            }));
            inactivityTimeline.setCycleCount(1); // Один цикл (20 секунд)
            inactivityTimeline.play();
        }
    }

    /**
     * Останавливает проверку бездействия и удаляет все таймеры.
     */
    public void stopInactivityCheck() {
        if (inactivityTimeline != null && inactivityTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
            inactivityTimeline.stop(); // Останавливаем таймер бездействия
        }
        if (countdownTimeline != null && countdownTimeline.getStatus().equals(Timeline.Status.RUNNING)) {
            countdownTimeline.stop(); // Останавливаем таймер обратного отсчета
        }
        isRunning = false; // Сбрасываем флаг
        System.out.println("Таймер бездействия остановлен");
    }

    /**
     * Показывает окно предупреждения с таймером.
     */
    public void showWarning() {
        Text warningText = new Text("Сессия будет отключена через 10 секунд\nЕсли вы еще тут, то нажмите кнопку 'Продолжить'");
        warningText.setStyle("-fx-font-size: 24px; -fx-fill: white;");

        Text countdownText = new Text("10");
        countdownText.setStyle("-fx-font-size: 48px; -fx-fill: white;");

        Button continueButton = new Button("Продолжить");
        continueButton.setStyle("""
            -fx-font-size: 24px;
            -fx-padding: 10px 20px;
            -fx-background-color: white;
            -fx-text-fill: red;
            -fx-border-color: red;
            -fx-border-width: 2px;
        """);

        VBox root = new VBox(20, warningText, countdownText, continueButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 50px;");

        Scene scene = new Scene(root, 1920, 1080); // Уменьшим размер для модального окна

        // Создаем модальное окно
        Stage warningStage = new Stage();
        warningStage.initOwner(primaryStage); // Привязываем к основному окну
        warningStage.initModality(Modality.APPLICATION_MODAL); // Делаем модальным
        warningStage.setScene(scene);
        warningStage.setTitle("Предупреждение");
        warningStage.setResizable(false); // Запрещаем изменение размера
        warningStage.show();

        countdown = 10;
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            countdown--;
            countdownText.setText(String.valueOf(countdown));
            if (countdown <= 0) {
                countdownTimeline.stop();
                warningStage.close();
                redirectToBlockWindow();
            }
        }));
        countdownTimeline.setCycleCount(10);
        countdownTimeline.play();

        continueButton.setOnAction(event -> {
            countdownTimeline.stop();
            warningStage.close();
            resetInactivityTimer();
        });
    }

    /**
     * Перенаправляет пользователя в BlockWindow.
     */
    private void redirectToBlockWindow() {
        primaryStage.hide(); // Скрываем текущее окно
        Stage blockStage = new Stage();
        BlockWindow blockWindow = new BlockWindow();
        try {
            blockWindow.start(blockStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Сбрасывает таймер бездействия.
     */
    public void resetInactivityTimer() {
        if (inactivityTimeline != null) {
            inactivityTimeline.stop(); // Останавливаем текущий таймер
        }
        inactivityTimeline = new Timeline(new KeyFrame(Duration.seconds(20), event -> {
            showWarning(); // Показываем предупреждение после 20 секунд бездействия
        }));
        inactivityTimeline.setCycleCount(1); // Один цикл (20 секунд)
        inactivityTimeline.play();
        System.out.println("Таймер бездействия сброшен");
    }
}