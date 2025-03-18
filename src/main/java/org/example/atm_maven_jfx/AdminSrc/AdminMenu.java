package org.example.atm_maven_jfx.AdminSrc;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.AdminSrc.Functions.ButtonUtil;
import org.example.atm_maven_jfx.AdminSrc.Functions.StatsUtil;
import org.example.atm_maven_jfx.AdminSrc.Window.Service.ServiceManagement;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.Incossations;
import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;
import org.example.atm_maven_jfx.Windows.MainMenu.MainMenu;

import java.net.URL;

public class AdminMenu {

    public Scene createScene(Stage primaryStage) {
        // Основной контейнер
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.setTranslateY(-600);
        vbox.setTranslateX(-1300);

        // Загрузка фонового изображения
        URL resourceUrl = getClass().getResource("/org/example/atm_maven_jfx/Assets/header-bg-big.jpg");
        if (resourceUrl == null) {
            System.err.println("Ресурс не найден: /Assets/header-bg-big.jpg");
        } else {
            Image backgroundImage = new Image(resourceUrl.toExternalForm());
            BackgroundImage background = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            100, 100, true, true, true, true
                    )
            );
            vbox.setBackground(new Background(background)); // Устанавливаем фон для vbox
        }

        // Заголовок меню
        Text menuTitle = new Text("Admin Menu");
        menuTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
        menuTitle.setFill(Color.WHITE);
        vbox.getChildren().add(menuTitle);

        // Стили для кнопок
        String defaultStyle = "-fx-text-fill: black; -fx-background-color: white; -fx-border-color: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;";
        String hoverStyle = "-fx-text-fill: white; -fx-background-color: transparent; -fx-border-color: white; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;";
        String pressedStyle = "-fx-text-fill: white; -fx-background-color: red; -fx-border-color: red; -fx-font-size: 16px; -fx-min-width: 200px; -fx-min-height: 50px;";


        // Кнопка "Инкассация"
        Button incassationButton = ButtonUtil.createButton("Инкассация", defaultStyle, hoverStyle, pressedStyle);
        incassationButton.setOnAction(e -> {
            Incossations incossations = new Incossations();
            Scene incossationsScene = incossations.createScene(primaryStage, primaryStage.getScene()); // Передаем текущую сцену
            primaryStage.setScene(incossationsScene);
        });
        vbox.getChildren().add(incassationButton);
        // Кнопка "Настройки Среды"
        Button settingsButton = ButtonUtil.createButton("Настройки Среды", defaultStyle, hoverStyle, pressedStyle);
        settingsButton.setOnAction(e -> {
            ServiceManagement serviceManagement = new ServiceManagement(primaryStage, primaryStage.getScene());
            primaryStage.setScene(serviceManagement.getScene());
        });
        vbox.getChildren().add(settingsButton);
        // Кнопка "Открыть MainMenu"
        Button mainMenuButton = ButtonUtil.createButton("Открыть MainMenu", defaultStyle, hoverStyle, pressedStyle);
        mainMenuButton.setOnAction(e -> {
            // Пример номера карты (можно заменить на реальный)
            String cardNumber = "1234567891234567";
            MainMenu mainMenu = new MainMenu(primaryStage, primaryStage.getScene(), cardNumber);
            primaryStage.setScene(mainMenu.getScene());
        });
        vbox.getChildren().add(mainMenuButton);

        // Кнопка "Запустить"
        Button buttonStart = ButtonUtil.createButton("Запустить", defaultStyle, hoverStyle, pressedStyle);
        vbox.getChildren().add(buttonStart);

        buttonStart.setOnAction(e -> {
            BlockWindow blockWindow = new BlockWindow();
            Stage newStage = new Stage();
            blockWindow.start(newStage);
            primaryStage.close();
        });

        // Создание сцены
        Scene scene = new Scene(vbox, 1930, 1090);

        // Загрузка и отображение статистики
        StatsUtil.loadAndDisplayStats(primaryStage);

        return scene;
    }
}