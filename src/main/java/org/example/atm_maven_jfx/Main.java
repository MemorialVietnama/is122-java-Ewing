package org.example.atm_maven_jfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.AdminSrc.FirstAuth;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene previousScene = new Scene(new javafx.scene.layout.StackPane(), 400, 300);
        FirstAuth firstAuth = new FirstAuth(previousScene);
        firstAuth.start(primaryStage);
        primaryStage.setTitle("ATM Application"); // Добавлен заголовок для удобства
        primaryStage.show();
    }
}