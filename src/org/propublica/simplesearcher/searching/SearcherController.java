package org.propublica.simplesearcher.searching;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.propublica.simplesearcher.Configuration;

import java.awt.*;
import java.io.File;
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
            Platform.runLater(() -> {
                try {
                    ArrayList<Document> s = Searcher.search(textField.getText(), directoryReader);
                    listView.setItems(FXCollections.observableList(s));
                } catch (QueryNodeException | IOException e) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Parser error in query");
                    a.setContentText(e.getMessage());
                    a.showAndWait();
                }
            });
        });

        listView.setCellFactory((value) -> new ScoreDocCell());
        listView.setOnMouseClicked((value) -> {
            Document document = listView.getSelectionModel().getSelectedItem();
            if (value.getButton() == MouseButton.SECONDARY) {
                Platform.runLater(() -> {
                    try {
                        Stage stage = new Stage();
                        stage.setTitle("SimpleSearcher | Contents of " + document.get("filename"));
                        FXMLLoader text = new FXMLLoader(getClass().getResource("text.fxml"));
                        stage.setScene(new Scene(text.load()));
                        TextController textController = text.getController();
                        textController.displayText(document.get("text"));
                        stage.show();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                });
            } else if (value.getClickCount() == 2) {
                String relative = document.get("filename");
                File file = Configuration.getPath().resolve(relative).toFile();
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Desktop.getDesktop().open(file);
                        return null;
                    }
                };
                task.setOnFailed((v) -> {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Error");
                    a.setHeaderText("Could not open: " + file);
                    a.setContentText(v.toString());
                    a.showAndWait();
                });
                new Thread(task).start();
            }
        });
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
