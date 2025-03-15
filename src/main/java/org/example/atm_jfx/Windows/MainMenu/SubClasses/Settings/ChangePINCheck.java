package org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Settings.Interfaces.ChangePINCheckInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ChangePINCheck implements ChangePINCheckInterface {
    private final Scene scene;
    private final String cardNumber;

    public ChangePINCheck(Stage primaryStage, Scene previousScene, String cardNumber) {
        this.cardNumber = cardNumber;
        this.scene = createScene(primaryStage, previousScene);
    }

    @Override
    public Scene createScene(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: red; -fx-padding: 40px;");

        Label successLabel = new Label("PIN-код успешно изменен!");
        successLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-text-fill: red; -fx-font-size: 20px; -fx-background-color: white; -fx-cursor: hand;");
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));

        root.getChildren().addAll(successLabel, backButton);

        logOperation(cardNumber, "Успешная смена PIN-кода для карты " + cardNumber);
        root.setTranslateX(-400);
        root.setTranslateY(-300);
        return new Scene(root, 1920, 1080);
    }

    @Override
    public void logOperation(String cardNumber, String comment) {
        String url = "jdbc:firebirdsql://localhost:3050/C:/ATMV_MODEL_DBASE";
        String username = "SYSDBA";
        String password = "010802";

        String query = "INSERT INTO CLIENT_OPERATION (CARD_NUM, OPERATION, DDATE_STAMP, COMMENT) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, cardNumber);
            stmt.setString(2, "Смена PIN");
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, comment);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}
