package org.propublica.simplesearcher.indexing;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class IndexerController {
    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextFlow textFlow;

    @FXML
    private ScrollPane scrollPane;

    private final SimpleBooleanProperty done = new SimpleBooleanProperty(false);

    public void onDone(ChangeListener<Boolean> changeListener) {
        done.addListener(changeListener);
    }

    public void index() {
        final Indexer indexer = new Indexer();
        progressBar.progressProperty().bind(indexer.progressProperty());
        Text t = new Text("");
        textFlow.getChildren().add(t);
        indexer.messageProperty().addListener((observableValue, s, t1) -> {
            t.setText(indexer.getLog());
            scrollPane.setVvalue(1.0f);
        });
        indexer.setOnSucceeded((workerStateEvent) -> done.set(true));
        new Thread(indexer).start();
    }
}
