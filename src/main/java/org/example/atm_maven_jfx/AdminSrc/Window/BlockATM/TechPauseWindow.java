package org.example.atm_maven_jfx.AdminSrc.Window.BlockATM;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;

public class TechPauseWindow {

    public Scene createScene() {
        // Основной контейнер
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: red;"); // Устанавливаем красный фон

        // Загрузка изображения
        URL resourceUrl = getClass().getResource("/org/example/atm_maven_jfx/Assets/Service.png");
        if (resourceUrl == null) {
            System.err.println("Ресурс не найден: /Assets/Service.png");
        } else {
            Image serviceImage = new Image(resourceUrl.toExternalForm());
            ImageView imageView = new ImageView(modifyImageColors(serviceImage));
            imageView.setFitWidth(300); // Установка ширины изображения
            imageView.setFitHeight(300); // Установка высоты изображения
            vbox.getChildren().add(imageView);
        }

        // Текстовые элементы
        Text titleText = new Text("Банкомат Временно не работает!");
        titleText.setStyle("""
               -fx-font-family: Arial, Helvetica, sans-serif;
               -fx-font-size: 40;
               -fx-font-weight: bold;
               
                """);
        titleText.setFill(Color.WHITE);

        Text apologyText = new Text("Просим прощения от лица EvillBank");
        apologyText.setStyle("""
               -fx-font-family: Arial, Helvetica, sans-serif;
               -fx-font-size: 40;
               -fx-font-weight: bold;
               
                """);
        apologyText.setFill(Color.WHITE);

        Text supportText = new Text("Техническая поддержка: +7-900-555-35-35, Номер банкомата №123456");
        supportText.setStyle("""
               -fx-font-family: Arial, Helvetica, sans-serif;
               -fx-font-size: 40;
               -fx-font-weight: bold;
               
                """);
        supportText.setFill(Color.WHITE);

        // Добавление текстовых элементов в контейнер
        vbox.getChildren().addAll(titleText, apologyText, supportText);

        // Создание и возврат сцены
        return new Scene(vbox, 1920, 1080);
    }

    private Image modifyImageColors(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                // Удаление белого цвета (заменяем на прозрачный)
                if (color.equals(Color.WHITE)) {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                }
                // Замена черного цвета на белый
                else if (color.equals(Color.BLACK)) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
                // Оставляем остальные цвета без изменений
                else {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }

        return writableImage;
    }
}