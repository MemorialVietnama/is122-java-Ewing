package org.example.atm_maven_jfx.AdminSrc.Functions;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class BackgroundUtil {

    public static Background createBackground(String imagePath) {
        Image backgroundImage = new Image(imagePath);
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true));
        return new Background(background);
    }
}