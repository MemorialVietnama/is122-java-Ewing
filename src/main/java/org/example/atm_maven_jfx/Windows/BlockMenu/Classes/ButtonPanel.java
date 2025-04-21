package org.example.atm_maven_jfx.Windows.BlockMenu.Classes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Windows.AuthKeypadCard.AuthWithNumberCard;
import org.example.atm_maven_jfx.Windows.Biometry.BioAuthScene;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ButtonPanel {
    public HBox getView(Stage primaryStage) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);

        String defaultStyle = "-fx-text-fill: red;" +
                " -fx-background-color: white;" +
                " -fx-border-color: white;" +
                " -fx-font-size: 36px;" +
                " -fx-font-weight: bold;" +
                " -fx-color: red; " +
                " -fx-min-width: 250px;" +
                " -fx-border-radius: 5px;" +
                " -fx-min-height: 150px;";

        // Создание кнопки "Войти по номеру карты" с изображением
        Button button1 = new Button("Войти по номеру карты");
        Image keyboardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/keyboard.png")));
        ImageView keyboardImageView = new ImageView(keyboardImage);
        keyboardImageView.setFitWidth(50); // Установка ширины изображения
        keyboardImageView.setFitHeight(50); // Установка высоты изображения
        button1.setGraphic(keyboardImageView); // Добавление изображения на кнопку
        button1.setStyle(defaultStyle);
        button1.setEffect(shadow);
        button1.setOnAction(_ -> {
            System.out.println("Кнопка 'Войти по номеру карты' нажата!");
            AuthWithNumberCard authWithNumberCard = new AuthWithNumberCard(primaryStage, primaryStage.getScene());
            primaryStage.setScene(authWithNumberCard.getScene());
        });

        // Создание кнопки "Войти по биометрии" с изображением
        Button button3 = new Button("Войти по биометрии");
        Image bioImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/bio.png")));
        ImageView bioImageView = new ImageView(bioImage);
        bioImageView.setFitWidth(50); // Установка ширины изображения
        bioImageView.setFitHeight(50); // Установка высоты изображения
        button3.setGraphic(bioImageView); // Добавление изображения на кнопку
        button3.setStyle(defaultStyle);
        button3.setEffect(shadow);
        button3.setTranslateY(10);

        // Метка для отображения состояния доступности
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        statusLabel.setVisible(false); // По умолчанию скрыта

        // Проверка доступности сайта
        boolean isSiteAvailable = checkSiteAvailability();

        if (!isSiteAvailable) {
            button3.setDisable(true); // Деактивируем кнопку
            statusLabel.setText("Сейчас недоступно");
            statusLabel.setVisible(true); // Показываем метку
        }

        // Обработчик события кнопки
        button3.setOnAction(event -> {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = currentStage.getScene(); // Сохраняем текущую сцену

            // Создание новой сцены с передачей предыдущей
            Scene bioAuthScene = new BioAuthScene(currentStage, currentScene).createScene();

            // Установка новой сцены
            currentStage.setScene(bioAuthScene);
        });

        // Контейнер для кнопки и метки
        VBox bioButtonContainer = new VBox(5, button3, statusLabel);
        bioButtonContainer.setAlignment(Pos.CENTER);

        // Создание контейнера HBox для кнопок
        HBox buttonBox = new HBox(10, button1, bioButtonContainer);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setTranslateY(50);

        return buttonBox;
    }

    /**
     * Проверяет доступность сайта по указанному URL.
     *
     * @return true, если сайт доступен, иначе false
     */
    private boolean checkSiteAvailability() {
        try {
            URL url = new URL("http://127.0.0.1:8080/webcam");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // Таймаут в 3 секунды
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            // Логирование ошибки (можно заменить на использование логгера)
            System.err.println("Ошибк а при проверке доступности сайта: " + e.getMessage());
            return false;
        }
    }
}