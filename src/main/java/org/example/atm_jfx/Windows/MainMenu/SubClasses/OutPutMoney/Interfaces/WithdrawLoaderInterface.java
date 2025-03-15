package org.example.atm_jfx.Windows.MainMenu.SubClasses.OutPutMoney.Interfaces;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface WithdrawLoaderInterface {
    Scene getScene();
    void createTextAnimation();
    void animateText(String messageText, boolean isTyping);
    void switchMessage();
    String getRandomMessage();
}
