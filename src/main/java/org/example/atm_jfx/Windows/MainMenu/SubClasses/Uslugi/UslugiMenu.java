package org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_jfx.Functions.InfoPanel;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.ServiceMenu;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static javafx.geometry.Pos.CENTER;

public class UslugiMenu implements ServiceMenu {
    private final Scene scene;

    public UslugiMenu(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        this.scene = createScene(primaryStage, previousScene, cardNumber, balance);
    }

    private Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        StackPane root = new StackPane();
        HBox headerBox = new HBox(20);
        root.setStyle("-fx-background-color: red;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(-70);

        Label titleLabel = new Label("Услуги");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 30px;");
        Button backButton = new Button("Назад");
        backButton.setStyle("""
            -fx-text-fill: red;
            -fx-font-size: 30px;
            -fx-padding: 10px 30px;
            -fx-border-color: white;
            -fx-border-width: 2px;
            -fx-background-color: white;
        """);
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        List<String> serviceNames = Arrays.asList(
                "Оплата Интернета",
                "Оплата ЖКХ",
                "Пополнение STEAM",
                "Пополнение VK Games",
                "Оплата Счетов",
                "Пожертвуйте на Благотворительность"
        );
        Collections.shuffle(serviceNames);

        for (int i = 0; i < 6; i++) {
            Button serviceButton = new Button(serviceNames.get(i));
            serviceButton.setStyle("""
                -fx-text-fill: red;
                -fx-font-size: 30px;
                -fx-font-family: Arial;
                -fx-font-weight: bold;
                -fx-padding: 15px 30px;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-text-alignment: center;
            """);
            serviceButton.setPrefSize(300, 300);
            serviceButton.setWrapText(true);
            int finalI = i;
            serviceButton.setOnAction(event -> {
                SerialForm form = new SerialForm(
                        primaryStage,
                        cardNumber,
                        balance,
                        previousScene,
                        serviceNames.get(finalI),
                        "Введите сумму",
                        "Введите номер счета",
                        () -> {
                            System.out.println("Выбрана услуга: " + serviceNames.get(finalI));
                        }
                );
                primaryStage.setScene(form.getScene());
            });

            gridPane.add(serviceButton, i % 3, i / 3);
        }
        backButton.setEffect(shadow);

        headerBox.getChildren().addAll(backButton, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setTranslateY(-50);
        VBox mainPageBox = new VBox();
        mainPageBox.setAlignment(Pos.CENTER);
        mainPageBox.setStyle("-fx-background-color: red;");
        mainPageBox.getChildren().addAll(infoPanel, headerBox, gridPane);
        root.getChildren().addAll(mainPageBox);
        root.setTranslateX(-400);
        root.setTranslateY(-290);
        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }
}