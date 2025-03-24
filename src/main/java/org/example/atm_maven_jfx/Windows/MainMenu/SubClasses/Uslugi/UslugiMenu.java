package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.animation.FadeTransition;
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
import javafx.util.Duration;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning; // Импортируем SessionWarning
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.ServiceMenu;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static javafx.geometry.Pos.CENTER;

public class UslugiMenu implements ServiceMenu {
    private final Scene scene;
    private SessionWarning sessionWarning; // Поле для SessionWarning

    public UslugiMenu(Stage primaryStage, Scene previousScene, String cardNumber, double balance) {
        this.scene = createScene(primaryStage, previousScene, cardNumber, balance);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
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
        backButton.setOnAction(event -> {
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
            }
            SceneTransition.changeSceneWithAnimation(primaryStage, previousScene);
        });

        // Панель для кнопок услуг
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        // Получаем активные услуги из базы данных
        List<String> serviceNames;
        try {
            serviceNames = DatabaseService.getActiveServices();
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка услуг: " + e.getMessage());
            serviceNames = Collections.emptyList(); // В случае ошибки возвращаем пустой список
        }

        // Переменные для пагинации
        final int ITEMS_PER_PAGE = 6;
        final int totalPages = (int) Math.ceil((double) serviceNames.size() / ITEMS_PER_PAGE);
        final int[] currentPage = {1}; // Текущая страница

        // Метод для обновления кнопок услуг с анимацией
        List<String> finalServiceNames = serviceNames;
        Runnable updateButtons = () -> {
            // Создаем анимацию исчезновения
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), gridPane);
            fadeOut.setFromValue(1.0); // Начальная прозрачность
            fadeOut.setToValue(0.0);   // Конечная прозрачность
            fadeOut.setOnFinished(event -> {
                // После завершения исчезновения обновляем кнопки
                gridPane.getChildren().clear(); // Очищаем предыдущие кнопки

                int start = (currentPage[0] - 1) * ITEMS_PER_PAGE;
                int end = Math.min(start + ITEMS_PER_PAGE, finalServiceNames.size());

                for (int i = start; i < end; i++) {
                    Button serviceButton = new Button(finalServiceNames.get(i));
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
                    serviceButton.setEffect(shadow);
                    serviceButton.setWrapText(true);
                    int finalI = i;
                    serviceButton.setOnAction(clickEvent -> {
                        if (sessionWarning != null) {
                            sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                        }
                        SerialForm form = new SerialForm(
                                primaryStage,
                                cardNumber,
                                balance,
                                previousScene,
                                finalServiceNames.get(finalI),
                                "Введите сумму",
                                "Введите номер счета",
                                () -> {
                                    System.out.println("Выбрана услуга: " + finalServiceNames.get(finalI));
                                }
                        );
                        primaryStage.setScene(form.getScene());
                    });

                    gridPane.add(serviceButton, (i - start) % 3, (i - start) / 3);
                }

                // Создаем анимацию появления
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), gridPane);
                fadeIn.setFromValue(0.0); // Начальная прозрачность
                fadeIn.setToValue(1.0);   // Конечная прозрачность
                fadeIn.play();            // Запускаем анимацию
            });
            fadeOut.play(); // Запускаем анимацию исчезновения
        };

        // Панель управления страницами
        HBox paginationBox = new HBox(20);
        paginationBox.setAlignment(Pos.CENTER);
        paginationBox.setTranslateY(20);

        Button prevButton = new Button("<-");
        Button nextButton = new Button("->");
        Label pageLabel = new Label("Страница " + currentPage[0] + " из " + totalPages);

        prevButton.setStyle("-fx-font-size: 20px; -fx-padding: 5px 15px; -fx-background-color: white; -fx-text-fill: red;");
        nextButton.setStyle("-fx-font-size: 20px; -fx-padding: 5px 15px; -fx-background-color: white; -fx-text-fill: red;");
        pageLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        prevButton.setEffect(shadow);
        nextButton.setEffect(shadow);

        prevButton.setOnAction(event -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                pageLabel.setText("Страница " + currentPage[0] + " из " + totalPages);
                updateButtons.run();
                sessionWarning.checkInactivity(); // Сбрасываем таймер
            }
        });

        nextButton.setOnAction(event -> {
            if (currentPage[0] < totalPages) {
                currentPage[0]++;
                pageLabel.setText("Страница " + currentPage[0] + " из " + totalPages);
                updateButtons.run();
                sessionWarning.checkInactivity(); // Сбрасываем таймер
            }
        });

        paginationBox.getChildren().addAll(prevButton, pageLabel, nextButton);

        backButton.setEffect(shadow);

        headerBox.getChildren().addAll(backButton, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setTranslateY(-50);
        VBox mainPageBox = new VBox();
        mainPageBox.setAlignment(Pos.CENTER);
        mainPageBox.setStyle("-fx-background-color: red;");
        mainPageBox.getChildren().addAll(infoPanel, headerBox, gridPane, paginationBox);
        root.getChildren().addAll(mainPageBox);

        // Инициализация кнопок на первой странице
        updateButtons.run();

        return new Scene(root, 1920, 1080);
    }

    public Scene getScene() {
        return scene;
    }
}