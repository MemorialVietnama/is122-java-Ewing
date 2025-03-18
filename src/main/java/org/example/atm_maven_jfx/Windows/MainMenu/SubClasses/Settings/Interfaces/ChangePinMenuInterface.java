package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public interface ChangePinMenuInterface {
    Scene getScene();

    void showError(String errorMessage);

    GridPane createNumpad();

    boolean updatePinInDatabase(String cardNumber, String oldPin, String newPin);

    Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber);
}
