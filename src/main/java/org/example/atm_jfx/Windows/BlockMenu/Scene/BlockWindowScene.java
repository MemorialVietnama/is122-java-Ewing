package org.example.atm_jfx.Windows.BlockMenu.Scene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_jfx.Functions.InfoPanel;
import org.example.atm_jfx.Windows.BlockMenu.Classes.ButtonPanel;
import org.example.atm_jfx.Windows.BlockMenu.Classes.CardImageView;
import org.example.atm_jfx.Windows.BlockMenu.Classes.InstructionLabels;
import org.example.atm_jfx.Windows.BlockMenu.Classes.WelcomeLabel;
import org.example.atm_jfx.Windows.BlockMenu.Interface.ButtonActionHandler;
import org.example.atm_jfx.Windows.BlockMenu.Interface.SceneManager;
import org.example.atm_jfx.Windows.BlockMenu.Interface.StyleConfigurator;

public class BlockWindowScene implements SceneManager {
    private final StyleConfigurator styleConfigurator;

    public BlockWindowScene(ButtonActionHandler buttonActionHandler, StyleConfigurator styleConfigurator) {
        this.styleConfigurator = styleConfigurator;
    }

    @Override
    public Scene createScene(Stage primaryStage) {
        // Создаем панель с информацией
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setTranslateY(-100);

        // Создаем надпись "Добро Пожаловать"
        WelcomeLabel welcomeLabel = new WelcomeLabel();

        // Создаем изображение карты
        CardImageView cardImageView = new CardImageView();

        // Создаем инструкции
        InstructionLabels instructionLabels = new InstructionLabels();
        instructionLabels.setAlignment(Pos.CENTER);

        // Создаем панель кнопок
        ButtonPanel buttonPanel = new ButtonPanel() {
            @Override
            public HBox getView(Stage stage) {
                HBox buttonBox = super.getView(stage);

                // Настройка действий кнопок
                buttonBox.getChildren().forEach(button -> {
                    if (button instanceof Button) {
                        styleConfigurator.configureButtonStyle((Button) button);
                        ((Button) button).setEffect(styleConfigurator.createDropShadowEffect());
                    }
                });

                return buttonBox;
            }
        };

        VBox root = new VBox(
                infoPanel,
                welcomeLabel,
                cardImageView,
                instructionLabels,
                buttonPanel.getView(primaryStage)
        );

        root.setAlignment(Pos.CENTER);
        root.setSpacing(5);
        root.setStyle("-fx-background: linear-gradient(to bottom, red, red);");

        return new Scene(root, 1920, 1080);
    }
}