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
    private final StringBuffer logBuffer = new StringBuffer();

    Indexer(Path p) {
        path = p.toAbsolutePath();
        indexPath = p.resolve(".simplesearcher-index").toAbsolutePath();
    }

    private ArrayList<Path> findDocs(IndexReader reader) throws IOException {
        assert (Files.isDirectory(path));

        ArrayList<Path> docs = new ArrayList<>();

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                if (isCancelled()) return FileVisitResult.TERMINATE;
                if (path.equals(indexPath)) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    log("Starting Directory: " + path);
                    return FileVisitResult.CONTINUE;
                }
            }

            @Override
            public FileVisitResult visitFile(Path p, BasicFileAttributes basicFileAttributes) throws IOException {
                if (isCancelled()) return FileVisitResult.TERMINATE;
                if (reader.docFreq(new Term("filename", subPath(p).toString())) > 0) {
                    log("Already indexed: " + p);
                    return FileVisitResult.CONTINUE;
                }
                docs.add(p);
                return FileVisitResult.CONTINUE;
            }
        });

        return docs;
    }

    private void log(String s) {
        synchronized (logBuffer) {
            logBuffer.append("\n".concat(s));
        }
        updateMessage(s);
    }

    public synchronized String getLog() {
        return logBuffer.toString();
    }

    private Path subPath(Path p) {
        return p.subpath(path.getNameCount(), p.getNameCount());
    }

    @Override
    protected Void call() throws Exception {
        assert (Files.isDirectory(path));
        log("Indexing all documents in: " + path);
        log("Creating index in: " + indexPath);
        Directory index = FSDirectory.open(indexPath);
        Analyzer a = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(a);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter writer = new IndexWriter(index, iwc)) {
            ArrayList<Path> docs = findDocs(DirectoryReader.open(writer, false));
            double i = 1;
            for (Path path : docs) {
                final Date d = new Date();
                System.out.println(subPath(path));
                try {
                    Tika t = new Tika();
                    File f = new File(path.toString());
                    Document doc = new Document();
                    String c = t.parseToString(f);
                    doc.add(new Field("text", c, TextField.TYPE_STORED));
                    doc.add(new Field("filename", subPath(path).toString(), StringField.TYPE_STORED));
                    writer.addDocument(doc);
                } catch (TikaException e) {
                    log("Couldn't index: " + path + " reason: " + e.getMessage());
                }
                log("Finished indexing: " + path + " in " + ((new Date().getTime()) - d.getTime()) + "ms");
                updateProgress(i, docs.size());
                i++;
                if (isCancelled()) throw new IOException("Thread was cancelled.");
            }
        } catch (IOException e) {
            log("Couldn't build index: " + e.getMessage());
        }
        return null;
    }
}
