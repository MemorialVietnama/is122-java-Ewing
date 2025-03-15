package org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces;

import javafx.scene.Scene;

public interface SerialFormInterface {
    Scene getScene();
    boolean isValidAccount(String input);
    String sanitizeInput(String input);
}