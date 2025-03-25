package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning; // Импортируем SessionWarning
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.SettingsMenu;


public abstract class SettingsCardMenu implements SettingsMenu {
    private final Scene scene;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public SettingsCardMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.scene = createScene(primaryStage, previousScene, cardNumber);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setTranslateY(-50);
        root.setStyle("-fx-background-color: red;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setTranslateY(-50);

        ClientInfo clientInfo = DatabaseService.DatabaseHelper.getClientInfo(cardNumber);

        Label titleLabel = new Label("Настройки Аккаунта");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        VBox clientInfoBox = new VBox(10);
        clientInfoBox.setAlignment(Pos.CENTER_LEFT);
        clientInfoBox.setStyle("-fx-background-color: white; -fx-padding: 20px; -fx-max-width: 700px;");

        clientInfoBox.getChildren().add(new Label("Полное ФИО: " + clientInfo.fullName()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Возраст: " + clientInfo.age()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Пол: " + clientInfo.gender()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Дата рождения: " + clientInfo.dateOfBirth()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Паспорт: " + clientInfo.passport()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Где Выдан: " + clientInfo.wherePassport()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Дата выдачи паспорта: " + clientInfo.passportDate()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("СНИЛС: " + clientInfo.snils()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("ИНН: " + clientInfo.inn()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});
        clientInfoBox.getChildren().add(new Label("Статус Клиента: " + clientInfo.status()) {{
            setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;-fx-font-family: Arial;");
        }});

        Button backButton = new Button("Назад");
        backButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 30px;
                    -fx-padding: 10px 20px;
                    -fx-border-color: white;
                    -fx-font-weight: bold;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);
        backButton.setOnAction(_ -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            SceneTransition.changeSceneWithAnimation(primaryStage, previousScene); // Возвращаемся назад

        });

        VBox buttonBox = new VBox(5);

        Button changePin = new Button("Сменить PIN");
        changePin.setStyle("""
                
                -fx-text-fill: red;
                -fx-font-size: 30px;
                -fx-padding: 10px 20px;
                -fx-border-color: white;
                -fx-font-weight: bold;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-cursor: hand;
                
                """);

        Button statsClient = new Button("История Операций");
        statsClient.setStyle("""
                
                -fx-text-fill: red;
                -fx-font-size: 30px;
                -fx-padding: 10px 20px;
                -fx-border-color: white;
                -fx-font-weight: bold;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-cursor: hand;
                """);

        Button clientCards = new Button("Ваши карты");
        clientCards.setStyle("""
                
                -fx-text-fill: red;
                -fx-font-size: 30px;
                -fx-padding: 10px 20px;
                -fx-border-color: white;
                -fx-font-weight: bold;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-cursor: hand;
                """);

        buttonBox.getChildren().add(changePin);
        buttonBox.getChildren().add(statsClient);
        buttonBox.getChildren().add(clientCards);

        HBox mainPage = new HBox(10, clientInfoBox, buttonBox);
        mainPage.setAlignment(Pos.CENTER);

        changePin.setOnAction(_ -> {
            ChangePinMenu changePinMenu = new ChangePinMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(changePinMenu.getScene());

        });

        statsClient.setOnAction(_ -> {
            TransactionHistoryMenu transactionHistoryMenu = new TransactionHistoryMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(transactionHistoryMenu.getScene());
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });

        clientCards.setOnAction(_ -> {
            ClientCardsMenu clientCardsMenu = new ClientCardsMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(clientCardsMenu.getScene());
            sessionWarning.checkInactivity(); // Сбрасываем таймер
        });

        root.getChildren().addAll(infoPanel, titleLabel, mainPage, backButton);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }



    public record ClientInfo(String fullName, String age, String gender, String dateOfBirth, String passport,
                             String wherePassport, String passportDate, String snils, String inn, String status) {
        public String fullFio() {
            return null;
        }
    }
}