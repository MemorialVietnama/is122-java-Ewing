package org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.Uslugi.Interfaces.CustomKeyboardInterfaces;

public class CustomKeyboard implements CustomKeyboardInterfaces {
    private final TextField textField;
    private boolean isRussian = true;

    private final String[] englishLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private final String[] russianLetters = {"А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я"};
    private final String[] digits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public CustomKeyboard(TextField textField) {
        this.textField = textField;
    }

    public VBox createLetterKeyboard() {
        VBox keyboard = new VBox(5);
        keyboard.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        updateLetterKeyboard(grid);

        Button switchButton = createButton("Сменить Язык");
        switchButton.setOnAction(event -> {
            isRussian = !isRussian;
            updateLetterKeyboard(grid);
        });

        Button spaceButton = createButton(" ");
        spaceButton.setMinWidth(600);
        spaceButton.setOnAction(event -> textField.appendText(" "));

        Button clearInput = createButton("Стереть");
        clearInput.setOnAction(event -> textField.setText(""));

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setTranslateY(10);
        controlBox.getChildren().addAll(switchButton, spaceButton, clearInput);
        keyboard.getChildren().addAll(grid, controlBox);
        return keyboard;
    }

    public VBox createDigitKeyboard() {
        VBox keyboard = new VBox(15);
        keyboard.setAlignment(Pos.CENTER);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        updateDigitKeyboard(grid);
        keyboard.getChildren().addAll(grid);
        return keyboard;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setStyle("""
                -fx-font-family: 'Arial Black';
                -fx-font-weight: bold;
                -fx-text-fill: white;
                -fx-font-size: 20px;
                -fx-padding: 10px;
                -fx-min-width: 70px;
                -fx-min-height: 70px;
                -fx-background-color: red;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-cursor: hand;
                """);

        button.setOnMousePressed(event -> {
            button.setStyle("""
                    -fx-font-family: 'Arial Black';
                    -fx-font-weight: bold;
                    -fx-text-fill: black;
                    -fx-font-size: 20px;
                    -fx-padding: 10px;
                    -fx-min-width: 70px;
                    -fx-min-height: 70px;
                    -fx-background-color: white;
                    -fx-border-color: white;
                    -fx-border-width: 2px;
                    -fx-cursor: hand;
                    """);
        });

        button.setOnMouseReleased(event -> {
            button.setStyle("""
                -fx-font-family: 'Arial Black';
                -fx-font-weight: bold;
                -fx-text-fill: white;
                -fx-font-size: 20px;
                -fx-padding: 10px;
                -fx-min-width: 70px;
                -fx-min-height: 70px;
                -fx-background-color: red;
                -fx-border-color: white;
                -fx-border-width: 2px;
                -fx-cursor: hand;
                """);
        });

        return button;
    }

    private void addKeyButton(GridPane grid, String key, int row, int col) {
        Button button = createButton(key);
        button.setOnAction(event -> textField.appendText(button.getText()));
        grid.add(button, col, row);
    }

    private void updateLetterKeyboard(GridPane grid) {
        grid.getChildren().clear();
        String[] letters = isRussian ? russianLetters : englishLetters;

        int row = 0;
        int col = 0;
        for (String letter : letters) {
            addKeyButton(grid, letter, row, col);
            col++;
            if (col > 9) {
                col = 0;
                row++;
            }
        }
    }

    private void updateDigitKeyboard(GridPane grid) {
        grid.getChildren().clear();

        int row = 0;
        int col = 0;
        for (String digit : digits) {
            addKeyButton(grid, digit, row, col);
            col++;
        }
    }
}