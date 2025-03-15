package org.example.atm_jfx.Windows.MainMenu.SubClasses.Deposit.Interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface DepositOperationInterface {

    Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, int amount);

    String generateRandomSerias();

    Scene getScene();
}
