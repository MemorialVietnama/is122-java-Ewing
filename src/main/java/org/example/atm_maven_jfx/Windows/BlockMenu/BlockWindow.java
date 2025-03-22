package org.example.atm_maven_jfx.Windows.BlockMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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

        // Создаем предсцену с красным фоном
        StackPane preloaderPane = new StackPane();
        preloaderPane.setStyle("-fx-background-color: red;");
        Scene preloaderScene = new Scene(preloaderPane, 1920, 1080);
        primaryStage.setScene(preloaderScene);
        primaryStage.show();

        // Создаем реализации интерфейсов
        ButtonActionHandler buttonActionHandler = new ButtonActionHandlerImpl(primaryStage);
        StyleConfigurator styleConfigurator = new StyleConfiguratorImpl();

        // Создаем менеджер сцен
        SceneManager sceneManager = new BlockWindowScene(buttonActionHandler, styleConfigurator);

        // Создаем основную сцену
        Scene mainScene = sceneManager.createScene(primaryStage);

        // Переключаемся на основную сцену после небольшой задержки
        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        delay.setOnFinished(event -> primaryStage.setScene(mainScene));
        delay.play();
    }

    public Scene getScene() {
        // Возвращаем null, так как сцена создается в start
        return null;
    }

    // Метод для создания предсцены при переходе с других окон
    public static void showWithPreloader(Stage primaryStage) {
        StackPane preloaderPane = new StackPane();
        preloaderPane.setStyle("-fx-background-color: red;");
        Scene preloaderScene = new Scene(preloaderPane, 1920, 1080);
        primaryStage.setScene(preloaderScene);
        primaryStage.show();

        ButtonActionHandler buttonActionHandler = new ButtonActionHandlerImpl(primaryStage);
        StyleConfigurator styleConfigurator = new StyleConfiguratorImpl();
        SceneManager sceneManager = new BlockWindowScene(buttonActionHandler, styleConfigurator);
        Scene mainScene = sceneManager.createScene(primaryStage);

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
        delay.setOnFinished(event -> primaryStage.setScene(mainScene));
        delay.play();
    }
}