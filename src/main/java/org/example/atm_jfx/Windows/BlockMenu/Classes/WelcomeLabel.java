package org.example.atm_jfx.Windows.BlockMenu.Classes;

import javafx.scene.control.Label;

public class WelcomeLabel extends Label {
    public WelcomeLabel() {
        super("Добро пожаловать в EvilBank!");
        setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
    }
}