package org.example.atm_jfx.Windows.AuthKeypadCard.Interface;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface SceneCreator {
    Scene createScene(Stage primaryStage, Scene previousScene);
}