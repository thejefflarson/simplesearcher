package org.propublica.simplesearcher.indexing;

import javafx.concurrent.Task;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;

public class Indexer extends Task<Void> {
    private final Path path;
    private final Path indexPath;

    Indexer(Path path) {
        this.path = path;
        this.indexPath = path.resolve(".simplesearcher-index");
    }

    private ArrayList<Path> findDocs(IndexReader reader) throws IOException {
        if (!Files.isDirectory(path)) throw new IOException(path + "is not a directory!");

        ArrayList<Path> docs = new ArrayList<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                if (isCancelled()) return FileVisitResult.TERMINATE;
                if (path.equals(indexPath)) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    System.out.println("Starting Directory: " + path);
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                if (isCancelled()) return FileVisitResult.TERMINATE;
                if (reader.docFreq(new Term("filename", path.toString())) > 0) {
                    System.out.println("Already indexed: " + path);
                    return FileVisitResult.CONTINUE;
                }
                docs.add(path);
                return FileVisitResult.CONTINUE;
            }
        });

        return docs;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Indexing all documents in: " + path);
        final Path indexPath = path.resolve(".simplesearcher-index");
        System.out.println("Creating index in: " + indexPath);
        Directory index = FSDirectory.open(indexPath);
        Analyzer a = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(a);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(index, iwc)) {
            ArrayList<Path> docs = findDocs(DirectoryReader.open(writer, false));
            double i = 1;
            for (Path path : docs) {
                final Date d = new Date();

                try {
                    Tika t = new Tika();
                    File f = new File(path.toString());
                    Document doc = new Document();
                    String c = t.parseToString(f);
                    doc.add(new Field("text", c, TextField.TYPE_STORED));
                    doc.add(new Field("filename", path.toString(), StringField.TYPE_STORED));
                    writer.addDocument(doc);
                } catch (TikaException e) {
                    System.out.println("Couldn't index: " + path + " reason: " + e.getMessage());
                }
                updateMessage("Finished indexing: " + path + " in " + ((new Date().getTime()) - d.getTime()) + "ms");
                updateProgress(i, docs.size());
                i++;
                if (isCancelled()) throw new IOException("Thread was cancelled.");
            }
        } catch (IOException e) {
            System.out.println("Couldn't build index: " + e.getMessage());
        }
        return null;
    }
}
