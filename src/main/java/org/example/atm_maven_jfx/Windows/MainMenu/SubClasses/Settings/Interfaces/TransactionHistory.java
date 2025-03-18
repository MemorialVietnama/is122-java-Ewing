package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.TransactionHistoryMenu;

public interface TransactionHistory {
    Scene getScene();

    ObservableList<TransactionHistoryMenu.OperationInfo> loadOperationData(String cardNumber);
}