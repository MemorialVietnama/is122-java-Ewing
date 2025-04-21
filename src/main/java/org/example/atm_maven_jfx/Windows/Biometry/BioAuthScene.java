package org.example.atm_maven_jfx.Windows.Biometry;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Logger;

public class BioAuthScene {
    private final Stage stage;
    private final Scene previousScene; // Предыдущая сцена
    private static final Logger logger = Logger.getLogger(BioAuthScene.class.getName());
    private static final String SERVER_URL = "http://127.0.0.1:8080"; // Базовый URL сервера

    public BioAuthScene(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;

        if (this.previousScene == null) {
            logger.warning("Предупреждение: Предыдущая сцена не указана.");
        }
    }

    public Scene createScene() {
        logger.info("Создание сцены биометрической авторизации...");

        // Создание WebView
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Настройка пользовательского агента
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        // Загрузка страницы
        String url = SERVER_URL + "/webcam";
        webEngine.load(url);

        // Обработка события загрузки страницы
        webEngine.getLoadWorker().stateProperty().addListener((_, _, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                logger.info("Страница успешно загружена.");

                // Регистрация колбэка для получения данных из JavaScript
                webEngine.executeScript(
                        "window.javaCallback = function(name, card) {" +
                                "   window.alert('JavaFX received: Name=' + name + ', Card=' + card);" +
                                "};"
                );

                // Получение данных через JavaScript
                Object result = webEngine.executeScript("document.getElementById('dataElement')?.innerText");
                if (result != null) {
                    logger.info("Полученные данные: " + result);
                } else {
                    logger.warning("Элемент с данными не найден.");
                }
            }
        });

        // Обработка ошибок загрузки
        webEngine.getLoadWorker().exceptionProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                logger.severe("Ошибка загрузки страницы: " + newValue.getMessage());
            }
        });

        // Обработка событий от JavaScript
        webEngine.setOnAlert(event -> {
            String alertMessage = event.getData();
            logger.info("Получено сообщение от JavaScript: " + alertMessage);

            // Парсинг данных (например, ФИО и номер карты)
            if (alertMessage.startsWith("JavaFX received:")) {
                String[] parts = alertMessage.split(",");
                if (parts.length == 2) {
                    String name = parts[0].split("=")[1].trim();
                    String card = parts[1].split("=")[1].trim();
                    logger.info("Получены данные: ФИО=" + name + ", Номер карты=" + card);

                    // Здесь можно обновить UI или выполнить другие действия
                    Platform.runLater(() -> {
                        // Создаем сцену PinCodeForBiometry
                        PinCodeForBiometry pinCodeScene = new PinCodeForBiometry(stage, card);
                        Scene pinCodeSceneInstance = pinCodeScene.getScene();

                        // Переход на сцену ввода пин-кода
                        stage.setScene(pinCodeSceneInstance);
                    });
                }
            }
        });

        // Кнопка "Назад"
        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-padding: 10 20; -fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOnAction(_ -> {
            if (previousScene != null) {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), webView.getParent());
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(_ -> stage.setScene(previousScene));
                fadeOut.play();
            } else {
                logger.severe("Предыдущая сцена не определена.");
            }
        });

        // Создание корневого контейнера
        VBox root = new VBox(webView, backButton);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        root.setStyle("-fx-background-color: red;");

        return new Scene(root, 1920, 1080);
    }
}