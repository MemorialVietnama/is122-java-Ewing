package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.List;

import static org.example.atm_maven_jfx.Database.DatabaseService.getCashToRemove;

public class RemoveMoneyAction {

    public static Scene createScene(Stage primaryStage, Scene returnScene, int amountToRemove) {
        Label instructionLabel = new Label("Произведите вывод данных банкнот");
        instructionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px;");

        TableView<CashStorage> tableView = new TableView<>();
        tableView.setPrefSize(800, 400);

        TableColumn<CashStorage, String> idCashColumn = new TableColumn<>("ID Cash");
        idCashColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().idCash()));

        TableColumn<CashStorage, String> idAtmColumn = new TableColumn<>("ID ATM");
        idAtmColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().idAtm()));

        TableColumn<CashStorage, String> denominationsColumn = new TableColumn<>("Номинал");
        denominationsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().denominations()));

        TableColumn<CashStorage, String> serialNumberColumn = new TableColumn<>("Серийный номер");
        serialNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().serialNumber()));

        TableColumn<CashStorage, Timestamp> dateInsertedColumn = new TableColumn<>("Дата внесения");
        dateInsertedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().dateInserted()));

        tableView.getColumns().addAll(idCashColumn, idAtmColumn, denominationsColumn, serialNumberColumn, dateInsertedColumn);

        List<CashStorage> cashToRemove = getCashToRemove(amountToRemove);
        tableView.getItems().addAll(cashToRemove);

        Button nextButton = new Button("Далее");
        nextButton.setStyle("""
                    -fx-text-fill: red;
                    -fx-font-size: 20px;
                    -fx-padding: 5px 10px;
                    -fx-border-color: white;
                    -fx-font-weight: bold;
                    -fx-border-width: 2px;
                    -fx-background-color: white;
                    -fx-cursor: hand;
                """);
        nextButton.setOnAction(event -> {
            Scene loadingRemoveMoneyScene = LoadingRemoveMoney.createScene(primaryStage, returnScene, cashToRemove);
            primaryStage.setScene(loadingRemoveMoneyScene);
        });

        Button backButton = new Button("Назад");
        backButton.setStyle(nextButton.getStyle());
        backButton.setOnAction(event -> primaryStage.setScene(returnScene));

        tableView.setPrefWidth(800);
        tableView.setMaxWidth(800);

        VBox layout = new VBox(20, instructionLabel, tableView, nextButton, backButton);
        layout.setTranslateX(-400);
        layout.setTranslateY(-400);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: red;");

        return new Scene(layout, 1920, 1080);
    }

    public record CashStorage(String idCash, String idAtm, String denominations, String serialNumber,
                              Timestamp dateInserted) {
    }
}