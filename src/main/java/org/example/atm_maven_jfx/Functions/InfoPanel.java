package org.example.atm_maven_jfx.Functions;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoPanel extends HBox implements InfoPanelInterface {

    private final Label dateTimeLabel;
    private final Label atmNumberLabel;
    private final Label supportNumberLabel;

    public InfoPanel() {
        dateTimeLabel = createLabel("", 24);
        atmNumberLabel = createLabel("Номер АТМ: 123456", 18);
        supportNumberLabel = createLabel("Служба поддержки: 8-800-555-35-35", 18);

        setupPanel();
        startDateTimeUpdates();
    }

    private Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-family: 'Arial';" +
                        "-fx-font-size: " + fontSize + "px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );
        return label;
    }

    private void setupPanel() {
        this.setSpacing(20);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(dateTimeLabel, atmNumberLabel, supportNumberLabel);
    }

    private void startDateTimeUpdates() {
        updateDateTime();
        Timeline dateTimeTimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> updateDateTime()));
        dateTimeTimeline.setCycleCount(Timeline.INDEFINITE);
        dateTimeTimeline.play();
    }

    @Override
    public void updateDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        dateTimeLabel.setText(formatter.format(date));
    }

    @Override
    public HBox getInfoPanel() {
        return this;
    }

    @Override
    public Label getDateTimeLabel() {
        return dateTimeLabel;
    }

    @Override
    public Label getAtmNumberLabel() {
        return atmNumberLabel;
    }

    @Override
    public Label getSupportNumberLabel() {
        return supportNumberLabel;
    }
}