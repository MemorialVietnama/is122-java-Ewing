package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface ChangePINCheckInterface {
    Scene getScene();

    void logOperation(String cardNumber, String comment);

    Scene createScene(Stage primaryStage, Scene previousScene);
}
