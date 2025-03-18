package org.example.atm_maven_jfx.Windows.BlockMenu.Style;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import org.example.atm_maven_jfx.Windows.BlockMenu.Interface.StyleConfigurator;

public class StyleConfiguratorImpl implements StyleConfigurator {
    @Override
    public void configureButtonStyle(Button button) {
        button.setStyle(getButtonDefaultStyle());
    }

    @Override
    public DropShadow createDropShadowEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        return shadow;
    }

    @Override
    public String getButtonDefaultStyle() {
        return "-fx-text-fill: red;" +
                " -fx-background-color: white;" +
                " -fx-border-color: white;" +
                " -fx-font-size: 36px;" +
                " -fx-font-weight: bold;" +
                " -fx-min-width: 250px;" +
                " -fx-border-radius: 5px;" +
                " -fx-min-height: 150px;";
    }
}