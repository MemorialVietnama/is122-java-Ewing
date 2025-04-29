package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class LoadingRemoveMoney {

    public static Scene createScene(Stage primaryStage, Scene returnScene) {
        List<String> phrases = Arrays.asList(
                "Обработка данных...",
                "Проверка кассет...",
                "Загрузка данных..."
        );

        Label animatedLabel = new Label();
        animatedLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold");

        // Анимация текста
        Timeline timeline = new Timeline();
        int delay = 0;
        for (String phrase : phrases) {
            for (int i = 0; i <= phrase.length(); i++) {
                int finalI = i;
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delay), _ -> animatedLabel.setText(phrase.substring(0, finalI))));
                delay += 100;
            }
            delay += 1000;
        }

        // Выполнение операции с базой и возврат на предыдущую сцену
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(delay), _ -> primaryStage.setScene(returnScene)));

        timeline.setCycleCount(1);
        timeline.play();

        VBox layout = new VBox(animatedLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: red;");

        return new Scene(layout, 1920, 1080);
    }
}