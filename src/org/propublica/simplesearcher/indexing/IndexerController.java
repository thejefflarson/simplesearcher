package org.propublica.simplesearcher.indexing;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.nio.file.Path;

public class IndexerController {
    @FXML
    private ProgressBar progress;

    @FXML
    private TextFlow textFlow;

    @FXML
    private ScrollPane scrollPane;

    public void index(Path path) {
        final Indexer indexer = new Indexer(path);
        progress.progressProperty().bind(indexer.progressProperty());
        Text t = new Text("");
        textFlow.getChildren().add(t);
        indexer.messageProperty().addListener((observableValue, s, t1) -> {
            t.setText(indexer.getLog());
            scrollPane.setVvalue(1.0f);
        });
        new Thread(indexer).start();
    }
}
