package org.example.atm_maven_jfx.Windows.BlockMenu.Handler;

import javafx.stage.Stage;
import org.example.atm_maven_jfx.Windows.AuthKeypadCard.AuthWithNumberCard;
import org.example.atm_maven_jfx.Windows.BlockMenu.Interface.ButtonActionHandler;

public class ButtonActionHandlerImpl implements ButtonActionHandler {
    private final Stage primaryStage;

    public ButtonActionHandlerImpl(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void handleAuthWithNumberCard() {
        System.out.println("Кнопка 'Войти по номеру карты' нажата!");
        AuthWithNumberCard authWithNumberCard = new AuthWithNumberCard(primaryStage, primaryStage.getScene());
        primaryStage.setScene(authWithNumberCard.getScene());
    }

    @Override
    public void handleAuthWithCardScan() {
        System.out.println("Кнопка 'Войти по скану карты' нажата!");
    }

    @Override
    public void handleAuthWithBiometrics() {
        System.out.println("Кнопка 'Войти по биометрии' нажата!");
    }
}