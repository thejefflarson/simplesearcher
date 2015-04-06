package org.propublica.simplesearcher.searching;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearcherController implements Initializable {
    @FXML
    private TextField textField;

    @FXML
    private ListView<Document> listView;

    @FXML
    private Button button;

    private DirectoryReader directoryReader;

    public void setDirectoryReader(DirectoryReader d) {
        directoryReader = d;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button.setDefaultButton(true);
        button.setOnAction((value) -> {
            assert (directoryReader != null);
            try {
                ArrayList<Document> s = Searcher.search(textField.getText(), directoryReader);
                listView.setItems(FXCollections.observableList(s));
            } catch (ParseException | IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Parser error in query");
                a.setContentText(e.getMessage());
                a.showAndWait();
            }
        });

        listView.setCellFactory((value) -> new ScoreDocCell());
    }

    static public class ScoreDocCell extends ListCell<Document> {
        @Override
        protected void updateItem(Document item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.get("filename"));
            }
        }
    }
}
