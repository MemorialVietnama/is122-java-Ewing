package org.example.atm_maven_jfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.AdminSrc.FirstAuth;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args); // Запуск JavaFX приложения
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Проверяем и создаём базу данных при запуске приложения
        org.example.atm_maven_jfx.DatabaseInitializer.checkAndCreateDatabase();

        // Создаем временную пустую сцену
        Scene previousScene = new Scene(new StackPane(), 400, 300);

        // Создаем экземпляр FirstAuth и передаем ему сцену
        FirstAuth firstAuth = new FirstAuth(previousScene);

        // Отображаем первый экран авторизации
        firstAuth.start(primaryStage);

        // Показываем главное окно
        primaryStage.show();
    }
}