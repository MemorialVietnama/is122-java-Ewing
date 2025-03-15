package org.example.atm_jfx.Windows.BlockMenu.Interface;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;

public interface StyleConfigurator {
    void configureButtonStyle(Button button);
    DropShadow createDropShadowEffect();
    String getButtonDefaultStyle();
}