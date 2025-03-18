package org.example.atm_maven_jfx.Windows.MainMenu;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.DepositOperation;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.OutputMoney;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.SettingsCardMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi.UslugiMenu;
import java.util.Objects;

public class MainMenu {
    private final String cardNumber; // Номер карты
    private final Scene scene;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public MainMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.cardNumber = cardNumber;
        this.scene = createScene(primaryStage); // Создаем сцену сразу

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage) {
        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(100);



        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        /*
         * Блок кнопок
         */
        Button windrawButton = new Button("Снять");
        windrawButton.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 48px;
                -fx-padding: 10px 30px;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-min-width: 600px;
                -fx-font-weight: bold;
                -fx-min-height: 350px;
            """);

        Button depositButton = new Button("Пополнить");
        depositButton.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 48px;
                -fx-padding: 10px 30px;
                -fx-border-color: white;
                -fx-font-weight: bold;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-min-width: 600px;
                -fx-min-height: 350px;
            """);

        Button uslugiButton = new Button("Услуги");
        uslugiButton.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 48px;
                -fx-padding: 10px 30px;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-font-weight: bold;
                -fx-background-color: white;
                -fx-min-width: 500px;
                -fx-min-height: 710px;
            """);

        Button logOut = new Button("Выйти");
        logOut.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 48px;
                -fx-padding: 10px 30px;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-background-color: white;
            """);
        logOut.setTranslateY(100);

        // Получаем баланс из базы данных
        double balance = DatabaseService.getCardBalance(cardNumber);

        // Создаем кнопку с маскированным номером карты и балансом
        Button nameCard = new Button(maskCardNumber(cardNumber, (float) balance));
        nameCard.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 22px;
                -fx-padding: 10px 20px;
                -fx-border-color: white;
                -fx-font-weight: bold;
                -fx-border-width: 2px;
                -fx-font-family: Arial;
                -fx-background-color: white;
            """);
        nameCard.setAlignment(Pos.CENTER); // Центрируем текст
        nameCard.setEffect(shadow);

        // Загружаем изображения для кнопок
        Image cardImageSettingIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/setting.png")));
        ImageView cardImageSetting = new ImageView(cardImageSettingIcon);
        cardImageSetting.setFitWidth(64);
        cardImageSetting.setFitHeight(64);

        Image cardImageWindrowIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/windraw.png")));
        ImageView cardImageWindrow = new ImageView(cardImageWindrowIcon);
        cardImageWindrow.setFitWidth(64);
        cardImageWindrow.setFitHeight(64);

        Image cardImageDepositIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/deposit.png")));
        ImageView cardImageDeposit = new ImageView(cardImageDepositIcon);
        cardImageDeposit.setFitWidth(64);
        cardImageDeposit.setFitHeight(64);

        Image cardImageUslugiIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/atm_maven_jfx/Assets/uslugi.png")));
        ImageView cardImageUslugi = new ImageView(cardImageUslugiIcon);
        cardImageUslugi.setFitWidth(64);
        cardImageUslugi.setFitHeight(64);

        // Устанавливаем изображения в кнопки
        nameCard.setGraphic(cardImageSetting);
        nameCard.setContentDisplay(ContentDisplay.RIGHT);
        nameCard.setTranslateY(-250);

        windrawButton.setGraphic(cardImageWindrow);
        windrawButton.setContentDisplay(ContentDisplay.LEFT);

        depositButton.setGraphic(cardImageDeposit);
        depositButton.setContentDisplay(ContentDisplay.LEFT);

        uslugiButton.setGraphic(cardImageUslugi);
        uslugiButton.setContentDisplay(ContentDisplay.LEFT);

        // Обработчики событий для кнопок
        windrawButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            OutputMoney outputMoney = new OutputMoney(primaryStage, scene, cardNumber, balance);
            Scene newScene = outputMoney.getScene();
            SceneTransition.changeSceneWithAnimation(primaryStage, newScene); // Используем анимацию
        });

        depositButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            DepositOperation depositOperation = new DepositOperation(primaryStage, scene, cardNumber, (int) balance);
            Scene newScene = depositOperation.getScene();
            SceneTransition.changeSceneWithAnimation(primaryStage, newScene); // Используем анимацию
        });

        uslugiButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            UslugiMenu uslugiMenu = new UslugiMenu(primaryStage, scene, cardNumber, balance);
            Scene newScene = uslugiMenu.getScene();
            SceneTransition.changeSceneWithAnimation(primaryStage, newScene); // Используем анимацию
        });

        nameCard.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            SettingsCardMenu settingsCardMenu = new SettingsCardMenu(primaryStage, scene, cardNumber, balance) {
                public ClientInfo getClientInfo(String cardNumber) {
                    return null;
                }
            };
            Scene newScene = settingsCardMenu.getScene();
            SceneTransition.changeSceneWithAnimation(primaryStage, newScene); // Используем анимацию
        });

        logOut.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            Stage blockStage = new Stage();
            BlockWindow blockWindow = new BlockWindow();
            try {
                blockWindow.start(blockStage); // Запускаем BlockWindow
                primaryStage.hide(); // Скрываем основное окно
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        // Остальная часть интерфейса
        HBox headerBpx = new HBox(20, logOut, infoPanel);
        headerBpx.setAlignment(Pos.TOP_CENTER);
        headerBpx.setSpacing(20);

        VBox LeftBlock = new VBox(10, windrawButton, depositButton);
        HBox mainFrame = new HBox(50, LeftBlock, uslugiButton);
        mainFrame.setAlignment(Pos.BOTTOM_CENTER);
        mainFrame.setTranslateY(100);

        VBox leftFrame = new VBox(10, headerBpx, mainFrame);
        VBox rightFrame = new VBox(10, nameCard);
        rightFrame.setAlignment(Pos.CENTER);

        HBox MainPage = new HBox(5, leftFrame, rightFrame);
        VBox rootPage = new VBox(MainPage);
        rootPage.setAlignment(Pos.TOP_RIGHT);
        rootPage.setTranslateX(220);
        rootPage.setTranslateY(10);

        StackPane root = new StackPane(rootPage);
        root.setStyle("-fx-background-color: red;");

        return new Scene(root, 1920,1080);
    }

    public Scene getScene() {
        return scene;
    }

    private String maskCardNumber(String cardNumber, float balance) {
        if (cardNumber == null || cardNumber.length() <= 6) {
            return cardNumber; // Если длина меньше или равна 6, возвращаем как есть
        }
        String visiblePart = cardNumber.substring(0, 6); // Берем первые 6 символов
        String maskedPart = "*".repeat(cardNumber.length() - 6); // Генерируем "*" для остальной части
        return "Карта: " + visiblePart + maskedPart + "\nБаланс: " + balance + " кредитов";
    }

}