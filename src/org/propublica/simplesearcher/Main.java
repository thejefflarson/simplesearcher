package org.propublica.simplesearcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.lucene.index.DirectoryReader;
import org.propublica.simplesearcher.indexing.IndexerController;
import org.propublica.simplesearcher.searching.SearcherController;

import java.io.File;
import java.io.IOException;

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

        Configuration.setPath(file.toPath());

        FXMLLoader indexer = new FXMLLoader(getClass().getResource("indexing/indexer.fxml"));
        Parent root = indexer.load();
        primaryStage.setTitle("Simple Searcher");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        IndexerController c = indexer.getController();
        c.onDone((observableValue, o, t1) -> {
            try {
                FXMLLoader searcher = new FXMLLoader(getClass().getResource("searching/searcher.fxml"));
                Parent s = searcher.load();
                SearcherController sc = searcher.getController();
                sc.setDirectoryReader(DirectoryReader.open(Configuration.getDirectory()));
                primaryStage.setScene(new Scene(s));
                primaryStage.show();
            } catch (IOException e) {
                System.out.println("ugh");
            }
        });
        c.index();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
