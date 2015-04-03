package org.propublica.simplesearcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.propublica.simplesearcher.indexing.IndexerController;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("indexing/indexer.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Simple Searcher");
        primaryStage.setScene(new Scene(root, 400, 200));
        primaryStage.show();

        final Path p = Paths.get("");
        IndexerController c = loader.getController();
        c.setStage(primaryStage);
        c.index(p);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
