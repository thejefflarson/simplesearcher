package org.propublica.simplesearcher.indexing;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.nio.file.Path;

public class IndexerController {
    @FXML
    private ProgressBar progress;

    @FXML
    private TextFlow textFlow;

    @FXML
    private ScrollPane scrollPane;

    private Indexer indexer;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void index(Path path) {
        this.indexer = new Indexer(path);
        progress.progressProperty().bind(indexer.progressProperty());
        indexer.messageProperty().addListener(changeEvent -> {
            textFlow.getChildren().add(new Text("\n" + indexer.getMessage()));
            scrollPane.setVvalue(1.0f);
        });
        new Thread(indexer).start();
    }
}
