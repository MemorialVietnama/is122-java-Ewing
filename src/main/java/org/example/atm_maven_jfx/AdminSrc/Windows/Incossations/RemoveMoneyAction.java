package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RemoveMoneyAction {
    public static Scene createScene(Stage primaryStage, Scene nextScene) {
        Label instructionLabel = new Label("Произведите изъятие");
        instructionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        Button continueButton = new Button("Продолжить");
        continueButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 20px;
                    -fx-padding: 5px 10px;
                    -fx-border-color: white;
                    -fx-font-weight: bold;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);
        continueButton.setOnAction(_ -> {
            // Переход на сцену загрузки с анимацией
            Scene loadingScene = LoadingRemoveMoney.createScene(primaryStage, nextScene);
            primaryStage.setScene(loadingScene);
        });

        VBox layout = new VBox(20, instructionLabel, continueButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: red;");

        return new Scene(layout, 1920, 1080);
    }
}