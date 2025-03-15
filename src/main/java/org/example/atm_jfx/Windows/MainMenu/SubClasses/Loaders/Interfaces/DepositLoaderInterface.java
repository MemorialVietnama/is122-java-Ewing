package org.example.atm_jfx.Windows.MainMenu.SubClasses.Loaders.Interfaces;

import javafx.scene.Scene;

public interface DepositLoaderInterface {
    Scene getScene();
    void scheduleSceneChange(String cardNumber);
}
