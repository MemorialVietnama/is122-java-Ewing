package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces;

import javafx.scene.Scene;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.SettingsCardMenu;

public interface SettingsMenu {
    Scene getScene();

    SettingsCardMenu.ClientInfo getClientInfo(String cardNumber);
}