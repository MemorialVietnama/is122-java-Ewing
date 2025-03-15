package org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces;

import javafx.scene.Scene;

public interface ServiceLoader {
    Scene getScene();
    boolean logTransaction();
    boolean updateBalance();
}