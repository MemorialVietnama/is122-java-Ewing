package org.example.atm_maven_jfx.AdminSrc;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.atm_maven_jfx.AdminSrc.Interface.AuthInterface;
import org.example.atm_maven_jfx.Database.DatabaseService;

import java.sql.SQLException;

public class FirstAuth extends Application implements AuthInterface {

    private Stage primaryStage;
    private GridPane authLayout;
    private final Scene previousScene;

    public FirstAuth(Scene previousScene) {
        this.previousScene = previousScene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(1930);
        primaryStage.setHeight(1090);
        primaryStage.centerOnScreen();

        createAuthLayout(previousScene);

        Scene scene = new Scene(authLayout, 1930, 1090);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createAuthLayout(Scene previousScene) {
        authLayout = new GridPane();
        authLayout.setAlignment(Pos.CENTER);
        authLayout.setHgap(10);
        authLayout.setVgap(10);
        authLayout.setPadding(new Insets(25, 25, 25, 25));

        // Загрузка изображения из ресурсов
        Image backgroundImage = new Image(getClass().getResource("/org/example/atm_maven_jfx/Assets/header-bg-big.jpg").toExternalForm());
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true));
        authLayout.setBackground(new Background(background));

        Label userName = new Label("Логин:");
        userName.setTextFill(Color.WHITE);
        userName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;");
        authLayout.add(userName, 0, 1);

        TextField userTextField = new TextField();
        userTextField.setPromptText("Введите логин");
        userTextField.setStyle(
                "-fx-text-inner-color: white; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 5px;"
        );
        authLayout.add(userTextField, 1, 1);

        Label pw = new Label("Пароль:");
        pw.setTextFill(Color.WHITE);
        pw.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: transparent;");
        authLayout.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Введите пароль");
        pwBox.setStyle(
                "-fx-text-inner-color: white; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: white; " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 5px;"
        );
        authLayout.add(pwBox, 1, 2);

        Button btn = new Button("Войти");
        btn.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: white; -fx-font-size: 24px; -fx-pref-width: 200px; -fx-pref-height: 50px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-border-color: white; -fx-font-size: 24px; -fx-pref-width: 200px; -fx-pref-height: 50px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: white; -fx-font-size: 24px; -fx-pref-width: 200px; -fx-pref-height: 50px;"));
        btn.setOnMousePressed(e -> btn.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-border-color: red; -fx-font-size: 24px; -fx-pref-width: 200px; -fx-pref-height: 50px;"));
        btn.setOnMouseReleased(e -> btn.setStyle("-fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: white; -fx-font-size: 24px; -fx-pref-width: 200px; -fx-pref-height: 50px;"));

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        authLayout.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        actiontarget.setFill(Color.WHITE);
        authLayout.add(actiontarget, 1, 6);

        Label connectionStatusLabel = new Label();
        connectionStatusLabel.setTextFill(Color.WHITE);
        authLayout.add(connectionStatusLabel, 1, 7);

        btn.setOnAction(_ -> {
            String user = userTextField.getText();
            String password = pwBox.getText();

            if (authenticateUser(user, password)) {
                try {
                    switchToAdminLayout(previousScene);
                } catch (SQLException e) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Ошибка при переключении на админ-панель");
                    e.printStackTrace();
                }
            } else {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Неверные учетные данные");
            }
        });
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return DatabaseService.authenticateUser(username, password);
    }

    @Override
    public void switchToAdminLayout(Scene previousScene) throws SQLException {
        AdminMenu adminMenu = new AdminMenu();
        Scene adminScene = adminMenu.createScene(primaryStage);
        primaryStage.setScene(adminScene);
        primaryStage.show();
    }

    public Scene getScene() {
        return null;
    }
}