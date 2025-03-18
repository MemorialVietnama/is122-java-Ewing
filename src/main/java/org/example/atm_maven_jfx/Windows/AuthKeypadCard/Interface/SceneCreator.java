package org.example.atm_maven_jfx.Windows.AuthKeypadCard.Interface;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface SceneCreator {
    Scene createScene(Stage primaryStage, Scene previousScene);
}