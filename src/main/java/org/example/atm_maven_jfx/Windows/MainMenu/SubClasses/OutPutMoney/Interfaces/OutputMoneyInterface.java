package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.Interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface OutputMoneyInterface {
    Scene getScene();

    void handleNumberInput(String digit);

    void processWithdrawal(Stage primaryStage, String cardNumber);
}
