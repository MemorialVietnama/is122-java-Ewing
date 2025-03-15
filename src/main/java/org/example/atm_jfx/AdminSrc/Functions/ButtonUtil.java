package org.example.atm_jfx.AdminSrc.Functions;

import javafx.scene.control.Button;

public class ButtonUtil {

    public static Button createButton(String text, String defaultStyle, String hoverStyle, String pressedStyle) {
        Button button = new Button(text);
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        button.setOnMousePressed(e -> button.setStyle(pressedStyle));
        button.setOnMouseReleased(e -> button.setStyle(hoverStyle));
        return button;
    }
}