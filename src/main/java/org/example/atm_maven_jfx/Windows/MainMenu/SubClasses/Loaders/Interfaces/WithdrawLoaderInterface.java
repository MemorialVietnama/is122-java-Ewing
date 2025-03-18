package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Loaders.Interfaces;

import javafx.scene.Scene;

public interface WithdrawLoaderInterface {
    Scene getScene();

    void scheduleSceneChange(String cardNumber);
}
