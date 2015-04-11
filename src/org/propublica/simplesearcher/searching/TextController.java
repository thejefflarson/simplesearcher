package org.propublica.simplesearcher.searching;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class TextController {
    @FXML
    private TextArea textArea;

    public void displayText(String text) {
        textArea.setText(text);
    }
}
