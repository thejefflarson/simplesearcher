package org.propublica.simplesearcher.indexing;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.nio.file.Path;

public class IndexerController {
    @FXML
    private ProgressBar progress;

    private Indexer indexer;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void index(Path path) {
        System.out.println("opoo");
        this.indexer = new Indexer(path);
        System.out.println(progress.toString());
        progress.progressProperty().bind(indexer.progressProperty());
        new Thread(indexer).start();
    }
}
