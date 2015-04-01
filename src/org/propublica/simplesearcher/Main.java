package org.propublica.simplesearcher;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode;

public class Main {

    public static void indexDocs(Path p, IndexWriter writer) {

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
            indexDocs(p, writer);
            writer.close();
        } catch (IOException e) {
            System.out.println("Couldn't build index: " + e.getMessage());
        }
    }
}
