package org.example.atm_maven_jfx.AdminSrc.Interface;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface AdminMenuInterface {

    Scene createScene(Stage primaryStage);

    void loadAndDisplayStats(Stage primaryStage);

}
