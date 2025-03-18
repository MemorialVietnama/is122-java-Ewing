package org.example.atm_maven_jfx.Windows.BlockMenu.Classes;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InstructionLabels extends VBox {
    public InstructionLabels() {
        Label step1 = new Label("1. Вставьте карту в считыватель.");
        Label step2 = new Label("2. Введите ваш PIN-код.");
        Label step3 = new Label("3. Выберите необходимую операцию.");

        step1.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        step2.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        step3.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        getChildren().addAll(step1, step2, step3);
        setSpacing(10);
    }
}
