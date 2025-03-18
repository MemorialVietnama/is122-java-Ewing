package org.example.atm_maven_jfx.AdminSrc.Functions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatsUtil {

    public static void loadAndDisplayStats(Stage primaryStage) {
        final String JDBC_URL = "jdbc:firebirdsql:localhost/3050:C:/ATMV_MODEL_DBASE";
        final String USER = "SYSDBA";
        final String PASSWORD = "010802";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ATM_STATS");

            if (resultSet.next()) {
                long clients = resultSet.getLong("CLIENTS");
                float sumOperation = resultSet.getFloat("SUM_OPERATION");
                int operations = resultSet.getInt("OPERATIONS");

                VBox statsLayout = new VBox(10);
                statsLayout.setAlignment(Pos.CENTER);
                statsLayout.setPadding(new Insets(25, 25, 25, 25));

                Text statsTitle = new Text("Статистика Банкомата");
                statsTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 24));
                statsTitle.setFill(Color.WHITE);
                statsLayout.getChildren().add(statsTitle);

                Label clientsLabel = new Label("Количество клиентов: " + clients);
                clientsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                statsLayout.getChildren().add(clientsLabel);

                Label sumLabel = new Label("Общая сумма операций: " + sumOperation);
                sumLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                statsLayout.getChildren().add(sumLabel);

                Label operationsLabel = new Label("Количество операций: " + operations);
                operationsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                statsLayout.getChildren().add(operationsLabel);

                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Статистика операций");

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>("Клиенты", clients));
                series.getData().add(new XYChart.Data<>("Сумма операций", sumOperation));
                series.getData().add(new XYChart.Data<>("Операции", operations));

                barChart.getData().add(series);
                statsLayout.getChildren().add(barChart);

                Scene statsScene = new Scene(statsLayout, 800, 600);
                primaryStage.setScene(statsScene); // Correct usage of setScene on Stage
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
