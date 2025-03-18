package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.interfaces;

import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;


public interface UIService {
    Scene createScene(Stage primaryStage, Scene previousScene);

    boolean displayAddMoneyDialog(TableView tableView);

    boolean displayRemoveMoneyDialog(TableView tableView);

    Scene createLoadingScene(Stage primaryStage, Scene returnScene);
}