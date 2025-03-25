package org.example.atm_maven_jfx.AdminSrc.Functions;

import javafx.scene.control.Button;

public class ButtonUtil {

    public static Button createButton(String text, String defaultStyle, String hoverStyle, String pressedStyle) {
        Button button = new Button(text);
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(_ -> button.setStyle(hoverStyle));
        button.setOnMouseExited(_ -> button.setStyle(defaultStyle));
        button.setOnMousePressed(_ -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(_ -> button.setStyle(hoverStyle));
        return button;
    }
}