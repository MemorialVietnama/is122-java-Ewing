package org.example.atm_maven_jfx.Windows.BlockMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.atm_maven_jfx.Windows.BlockMenu.Handler.ButtonActionHandlerImpl;
import org.example.atm_maven_jfx.Windows.BlockMenu.Interface.ButtonActionHandler;
import org.example.atm_maven_jfx.Windows.BlockMenu.Interface.SceneManager;
import org.example.atm_maven_jfx.Windows.BlockMenu.Interface.StyleConfigurator;
import org.example.atm_maven_jfx.Windows.BlockMenu.Scene.BlockWindowScene;
import org.example.atm_maven_jfx.Windows.BlockMenu.Style.StyleConfiguratorImpl;

public class BlockWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        // Создаем реализации интерфейсов
        ButtonActionHandler buttonActionHandler = new ButtonActionHandlerImpl(primaryStage);
        StyleConfigurator styleConfigurator = new StyleConfiguratorImpl();

        // Создаем менеджер сцен
        SceneManager sceneManager = new BlockWindowScene(buttonActionHandler, styleConfigurator);

        // Создаем и устанавливаем сцену
        Scene scene = sceneManager.createScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Scene getScene() {
        return null;
    }
}