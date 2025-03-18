package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.atm_maven_jfx.Database.DatabaseService;

import java.sql.SQLException;
import java.util.List;

public class LoadingRemoveMoney {

    public static Scene createScene(Stage primaryStage, Scene returnScene, List<RemoveMoneyAction.CashStorage> cashToRemove) {
        Label animatedLabel = new Label("Удаление купюр...");
        animatedLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold");

        // Удаляем купюры из базы данных
        try {
            DatabaseService.removeCashFromDatabase(cashToRemove);
        } catch (SQLException e) {
            e.printStackTrace();
            animatedLabel.setText("Ошибка при удалении купюр.");
        }

        // Анимация на 5 секунд
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            primaryStage.setScene(returnScene);
        }));
        timeline.setCycleCount(1);
        timeline.play();

        VBox layout = new VBox(animatedLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setTranslateX(-400);
        layout.setTranslateY(-400);
        layout.setStyle("-fx-background-color: red;");

        return new Scene(layout, 1920, 1080);
    }
}