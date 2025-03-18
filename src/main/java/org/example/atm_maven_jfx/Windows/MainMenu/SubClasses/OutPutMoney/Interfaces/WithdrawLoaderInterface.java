package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.Interfaces;

import javafx.scene.Scene;

public interface WithdrawLoaderInterface {
    Scene getScene();

    void createTextAnimation();

    void animateText(String messageText, boolean isTyping);

    void switchMessage();

    String getRandomMessage();
}
