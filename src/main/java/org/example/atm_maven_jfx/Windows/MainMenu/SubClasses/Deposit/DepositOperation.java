package org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Functions.InfoPanel;
import org.example.atm_maven_jfx.Functions.SceneTransition;
import org.example.atm_maven_jfx.Functions.SessionWarning;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.Interfaces.DepositOperationInterface;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Loaders.DepositLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DepositOperation implements DepositOperationInterface {
    private static final int MAX_DEPOSIT = 15000;
    private final Scene scene;
    private final List<Denomination> banknotes = new ArrayList<>();
    private TableView<Denomination> table;
    private Label totalAmountLabel;
    private int totalAmount = 0;
    private final SessionWarning sessionWarning; // Поле для SessionWarning

    public DepositOperation(Stage primaryStage, Scene previousScene, String cardNumber, int amount) {
        this.scene = createScene(primaryStage, previousScene, cardNumber, amount);

        // Создаем объект SessionWarning
        sessionWarning = new SessionWarning(primaryStage);

        // Запускаем проверку бездействия
        sessionWarning.checkInactivity();
    }

    @Override
    public Scene createScene(Stage primaryStage, Scene previousScene, String cardNumber, int amount) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: red;");

        InfoPanel infoPanel = new InfoPanel();
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setTranslateY(30);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        Label titleLabel = new Label("Выберите номинал для пополнения");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;");

        totalAmountLabel = new Label("Введенная сумма: " + totalAmount + " руб.");
        totalAmountLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-font-size: 18px;");
        table.setPrefWidth(800);
        table.setMaxWidth(800);
        table.setMaxHeight(300);

        TableColumn<Denomination, String> demonitionCol = new TableColumn<>("ДЕНОМИНАЦИЯ");
        demonitionCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().demonition())
        );

        TableColumn<Denomination, String> seriasCol = new TableColumn<>("СЕРИЯ");
        seriasCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().serias())
        );

        TableColumn<Denomination, Void> actionsCol = new TableColumn<>("ДЕЙСТВИЯ");
        actionsCol.setCellFactory(_ -> new TableCell<>() {
            private final Button deleteButton = new Button("Удалить");

            {
                deleteButton.setStyle("""
                        -fx-font-family: 'Arial Black';
                        -fx-font-weight: bold;
                        -fx-text-fill: red;
                        -fx-font-size: 18px;
                        -fx-padding: 5px 10px;
                        -fx-background-color: white;
                        -fx-border-color: red;
                        -fx-border-width: 2px;
                        -fx-cursor: hand;
                    """);
                deleteButton.setOnAction(_ -> {
                    Denomination rowData = getTableView().getItems().get(getIndex());
                    System.out.println("DEBUG: Удаление банкноты: " + rowData.demonition() + ", серия: " + rowData.serias());

                    int parsedDenomination = Integer.parseInt(rowData.demonition().replace(" руб.", ""));
                    System.out.println("DEBUG: Попытка вычесть сумму: " + parsedDenomination);

                    if (totalAmount - parsedDenomination >= 0) {
                        table.getItems().remove(rowData);
                        totalAmount -= parsedDenomination;
                        System.out.println("DEBUG: Общая сумма после удаления: " + totalAmount);

                        totalAmountLabel.setText("Введенная сумма: " + totalAmount + " руб.");
                        banknotes.remove(rowData);
                        System.out.println("DEBUG: Банкнота успешно удалена.");
                    } else {
                        System.err.println("DEBUG: Ошибка при удалении банкноты. Общая сумма не может быть отрицательной.");
                    }

                    sessionWarning.checkInactivity(); // Сбрасываем таймер
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        table.getColumns().add(demonitionCol);
        table.getColumns().add(seriasCol);
        table.getColumns().add(actionsCol);
        demonitionCol.setPrefWidth(200);
        seriasCol.setPrefWidth(200);
        actionsCol.setPrefWidth(150);

        GridPane buttonGrid = getGridPane();

        Button nextButton = new Button("Далее");
        nextButton.setStyle("""
                -fx-font-family: 'Arial Black';
                -fx-font-weight: bold;
                -fx-text-fill: red;
                -fx-font-size: 30px;
                -fx-padding: 10px 20px;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-background-color: white;
                -fx-cursor: hand;
            """);
        nextButton.setEffect(shadow);
        nextButton.setOnAction(_ -> {
            System.out.println("DEBUG: Нажата кнопка 'Далее'");
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                System.out.println("DEBUG: Таймер бездействия остановлен.");
            }
            DepositLoader depositLoader = new DepositLoader(primaryStage, cardNumber, totalAmount, banknotes);
            primaryStage.setScene(depositLoader.getScene());
            System.out.println("DEBUG: Переход к следующей сцене.");
        });

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
                -fx-font-family: 'Arial Black';
            """);
        backButton.setOnAction(_ -> {
            System.out.println("DEBUG: Нажата кнопка 'Назад'");
            if (sessionWarning != null) {
                sessionWarning.stopInactivityCheck(); // Останавливаем таймер текущей сцены
                System.out.println("DEBUG: Таймер бездействия остановлен.");
            }
            SceneTransition.changeSceneWithAnimation(primaryStage, previousScene); // Возвращаемся назад
            System.out.println("DEBUG: Переход к предыдущей сцене.");
        });

        root.getChildren().addAll(titleLabel, totalAmountLabel, table, buttonGrid, backButton, nextButton);
        return new Scene(root, 1920, 1080);
    }

    private GridPane getGridPane() {
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        int[] denominations = {50, 100, 500, 1000, 2000, 5000};
        for (int i = 0; i < denominations.length; i++) {
            Button button = new Button(denominations[i] + " руб.");
            button.setStyle("""
                    -fx-font-family: 'Arial Black';
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                    -fx-font-size: 24px;
                    -fx-padding: 15px;
                    -fx-min-width: 170px;
                    -fx-background-color: red;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-cursor: hand;
                """);
            int denomination = denominations[i];
            button.setOnAction(_ -> {
                System.out.println("DEBUG: Нажата кнопка с номиналом: " + denomination + " руб.");

                if (totalAmount + denomination <= MAX_DEPOSIT) {
                    String serias = generateRandomSerias();
                    System.out.println("DEBUG: Сгенерирована серия: " + serias);

                    Denomination newBanknote = new Denomination(denomination + " руб.", serias);
                    table.getItems().add(newBanknote);

                    totalAmount += denomination;
                    System.out.println("DEBUG: Общая сумма после добавления: " + totalAmount);

                    totalAmountLabel.setText("Введенная сумма: " + totalAmount + " руб.");
                    banknotes.add(newBanknote);
                    System.out.println("DEBUG: Банкнота добавлена: " + newBanknote.demonition() + ", серия: " + newBanknote.serias());
                } else {
                    System.err.println("DEBUG: Превышение максимальной суммы пополнения (" + MAX_DEPOSIT + " руб.). Текущая сумма: " + totalAmount);
                    totalAmountLabel.setText("Максимальная сумма пополнения - 15000 руб.");
                }
                sessionWarning.stopInactivityCheck(); // Сбрасываем таймер
            });
            buttonGrid.add(button, i % 3, i / 3);
        }
        return buttonGrid;
    }

    @Override
    public String generateRandomSerias() {
        Random random = new Random();
        StringBuilder series = new StringBuilder("SN");
        for (int i = 0; i < 13; i++) {
            series.append(random.nextInt(10));
        }
        return series.toString();
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public record Denomination(String demonition, String serias) {
    }
}