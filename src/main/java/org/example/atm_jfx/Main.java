package org.example.atm_jfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.atm_jfx.AdminSrc.FirstAuth;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene previousScene = new Scene(new javafx.scene.layout.StackPane(), 400, 300);

        FirstAuth firstAuth = new FirstAuth(previousScene);
        firstAuth.start(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
