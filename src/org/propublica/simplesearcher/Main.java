package org.propublica.simplesearcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.propublica.simplesearcher.indexing.IndexerController;

import java.io.File;
import java.nio.file.Path;

public class Main extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("SimpleSearcher: Choose a directory to index");

        final File file = directoryChooser.showDialog(primaryStage);

        if (file == null) {
            Platform.exit();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("indexing/indexer.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Simple Searcher");
        primaryStage.setScene(new Scene(root, 960, 400));
        primaryStage.show();

        final Path path = file.toPath();
        IndexerController c = loader.getController();
        c.setStage(primaryStage);
        c.index(path);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
