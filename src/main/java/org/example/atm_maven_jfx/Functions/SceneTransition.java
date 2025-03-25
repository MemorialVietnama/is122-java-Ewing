package org.example.atm_maven_jfx.Functions;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneTransition {

    /**
     * Метод для плавной смены сцены с анимацией затухания и появления с красным фоном.
     *
     * @param primaryStage Главная сцена (Stage).
     * @param newScene     Новая сцена, которую нужно показать.
     */
    public static void changeSceneWithAnimation(Stage primaryStage, Scene newScene) {
        // Устанавливаем красный фон для текущей сцены
        Scene currentScene = primaryStage.getScene();
        currentScene.setFill(Color.RED);

        // Анимация исчезновения текущей сцены
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentScene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(_ -> {
            // Устанавливаем красный фон для новой сцены
            newScene.setFill(Color.RED);
            primaryStage.setScene(newScene);

            // Восстанавливаем прозрачность корневого узла новой сцены
            newScene.getRoot().setOpacity(1.0);

            // Анимация появления новой сцены
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
}