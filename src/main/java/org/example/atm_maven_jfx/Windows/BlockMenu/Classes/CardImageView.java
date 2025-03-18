package org.example.atm_maven_jfx.Windows.BlockMenu.Classes;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class CardImageView extends ImageView {
    public CardImageView() {
        // Загрузка изображения из ресурсов
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/atm-card.png")));
        if (image.isError()) {
            System.out.println("Ошибка загрузки изображения: " + image.getException());
        }
        setImage(image);

        // Настройка размеров
        setFitWidth(300);
        setFitHeight(250);
        setPreserveRatio(true); // Сохранение пропорций

        // Перекрашиваем изображение в белый цвет
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0.0); // Убираем насыщенность (делаем черно-белым)
        colorAdjust.setBrightness(1.0); // Устанавливаем максимальную яркость (белый цвет)
        setEffect(colorAdjust);
    }
}