package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.Interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface DepositMenuInterface {

    Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, double balance);

    void handleNumberInput(String digit);

    void processDeposit(Stage primaryStage, String cardNumber);

    Scene getScene();
}
