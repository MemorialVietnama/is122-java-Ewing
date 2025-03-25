package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.SessionWarning;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientCardsMenu {
    private final Scene scene;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public ClientCardsMenu(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.scene = createScene(primaryStage, previousScene, cardNumber);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label titleLabel = new Label("Ваши Карты");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        // Создаем таблицу
        TableView<CardInfo> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Автоматическое растяжение колонок

        // Колонка для номера карты
        TableColumn<CardInfo, String> numberCardColumn = new TableColumn<>("Номер карты");
        numberCardColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().numberCard())
        );

        // Колонка для банка
        TableColumn<CardInfo, String> bankColumn = new TableColumn<>("Банк");
        bankColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().bank())
        );

        // Колонка для даты валидации
        TableColumn<CardInfo, String> validationColumn = new TableColumn<>("Действительна до");
        validationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().validation())
        );

        // Колонка для типа карты
        TableColumn<CardInfo, String> cardTypeColumn = new TableColumn<>("Тип карты");
        cardTypeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().cardType())
        );

        // Добавляем колонки в таблицу
        tableView.getColumns().addAll(numberCardColumn, bankColumn, validationColumn, cardTypeColumn);

        // Загружаем данные в таблицу
        ObservableList<CardInfo> cardData = loadCardData(cardNumber);
        tableView.setItems(cardData);
        tableView.setMaxWidth(500);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-background-color: white; -fx-cursor: hand;");
        backButton.setOnAction(_ -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            primaryStage.setScene(previousScene);
        });

        root.getChildren().addAll(titleLabel, tableView, backButton);
        return new Scene(root, 1920, 1080);
    }

    // Метод для загрузки данных о картах
    private ObservableList<CardInfo> loadCardData(String cardNumber) {
        ObservableList<CardInfo> cardData = FXCollections.observableArrayList();
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String username = "SYSDBA";
        String password = "010802";

        String query = "SELECT NUMBER_CARD, FK_CARD_BANK, VALIDATION, FK_TYPE_CARD FROM CLIENT_CARD WHERE FK_CLIENT = (SELECT FK_CLIENT FROM CLIENT_CARD WHERE NUMBER_CARD = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();

            // Чтение данных из ResultSet
            while (rs.next()) {
                String numberCard = rs.getString("NUMBER_CARD");
                String bank = rs.getString("FK_CARD_BANK");
                String validation = rs.getDate("VALIDATION").toString();
                String cardType = rs.getString("FK_TYPE_CARD");

                // Добавляем данные в список
                cardData.add(new CardInfo(numberCard, bank, validation, cardType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cardData;
    }

    public Scene getScene() {
        return scene;
    }

    // Класс для хранения данных о карте
        public record CardInfo(String numberCard, String bank, String validation, String cardType) {
    }
}