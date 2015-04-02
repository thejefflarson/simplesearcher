package org.propublica.simplesearcher;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode;

public class Main {

    public static void findDocs(Path p, IndexWriter writer) throws IOException {
        if(Files.isDirectory(p)) { // shld only take a directory tbh
            Files.walkFileTree(p, new  FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    System.out.println("Starting: " + path);
                    return null;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    Date d = new Date();

                    System.out.println("Finished indexing: " + path + " in " + (d.getTime() - (new Date().getTime())) + "ms");
                    return null;
                }

                @Override
                public FileVisitResult visitFileFailed(Path path, IOException e) {
                    return null;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                    System.out.println("Finished: " + path);
                    return null;
                }
            });
        }
    }

    public static void main(String[] args) {
        final Path p = Paths.get("").toAbsolutePath();
        System.out.println("Indexing all documents in: " + p);

        final Path indexPath = p.resolve(".index");
        System.out.println("Creating index in: " + indexPath);

        try {
            Directory index = FSDirectory.open(p);
            Analyzer a = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(a);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(index, iwc);
            findDocs(p, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't build index: " + e.getMessage());
        }
    }
}
