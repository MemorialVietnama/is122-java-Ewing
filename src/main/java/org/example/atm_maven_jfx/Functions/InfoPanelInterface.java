package org.example.atm_maven_jfx.Functions;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public interface InfoPanelInterface {
    void updateDateTime();

    HBox getInfoPanel();

    Label getDateTimeLabel();

    Label getAtmNumberLabel();

    Label getSupportNumberLabel();
}