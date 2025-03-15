package org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_jfx.Functions.InfoPanel;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.SettingsMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class SettingsCardMenu implements SettingsMenu {
    private final Scene scene;

    public SettingsCardMenu(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        this.scene = createScene(primaryStage, previousScene, cardNumber, balance);
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setTranslateY(-50);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setTranslateY(-50);

        ClientInfo clientInfo = DatabaseHelper.getClientInfo(cardNumber);

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
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));
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
        HBox mainPage = new HBox(10,clientInfoBox, buttonBox);
        mainPage.setAlignment(Pos.CENTER);

        changePin.setOnAction(event -> {
            ChangePinMenu changePinMenu = new ChangePinMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(changePinMenu.getScene());
        });

        statsClient.setOnAction(event -> {
            TransactionHistoryMenu transactionHistoryMenu = new TransactionHistoryMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(transactionHistoryMenu.getScene());
        });

        clientCards.setOnAction(event -> {
            ClientCardsMenu clientCardsMenu = new ClientCardsMenu(primaryStage, scene, cardNumber);
            primaryStage.setScene(clientCardsMenu.getScene());
        });


        root.getChildren().addAll(infoPanel, titleLabel, mainPage, backButton);
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }

    public static class DatabaseHelper {
        static String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        static String username = "SYSDBA";
        static String password = "010802";


        public static ClientInfo getClientInfo(String cardNumber) {
            ClientInfo clientInfo = null;
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                String query = "SELECT * FROM CLIENT_INFO ci " +
                        "JOIN CLIENT_CARD cc ON ci.FULL_FIO = cc.FK_CLIENT " +
                        "WHERE cc.NUMBER_CARD = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, cardNumber);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        clientInfo = new ClientInfo(
                                rs.getString("FULL_FIO"),
                                rs.getString("AGE"),
                                rs.getString("GENGER"),
                                rs.getDate("DATA_BIRTH").toString(),
                                rs.getString("PASSPORT"),
                                rs.getString("WHERE_PASSPORT"),
                                rs.getDate("DATE_PASSPORT").toString(),
                                rs.getString("SNILS"),
                                rs.getString("INN"),
                                rs.getString("STATUS")
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clientInfo;
        }
    }

    public record ClientInfo(String fullName, String age, String gender, String dateOfBirth, String passport,
                             String wherePassport, String passportDate, String snils, String inn, String status) {
    }
}
