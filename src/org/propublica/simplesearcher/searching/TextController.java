package org.propublica.simplesearcher.searching;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextController {
    @FXML
    private TextFlow text;

    public void displayText(String message) {
        text.getChildren().add(new Text(message));
    }
}
