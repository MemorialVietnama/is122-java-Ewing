package org.example.atm_maven_jfx.Windows.AuthKeypadCard.Interface;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public interface KeyboardInterface {
    VBox createKeyboard(TextField cardNumberField, String primaryStage);
}