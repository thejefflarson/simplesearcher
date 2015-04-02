package org.propublica.simplesearcher;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import static org.apache.lucene.index.IndexWriterConfig.OpenMode;

public class Main {

    private static void findDocs(Path p, Path indexPath, IndexWriter writer) throws IOException {
        if(Files.isDirectory(p)) {
            // shld only take a directory tbh
            Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    if(p.equals(indexPath)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    } else {
                        System.out.println("Starting Directory: " + path);
                        return FileVisitResult.CONTINUE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    final Date d = new Date();
                    Tika t = new Tika();
                    File f = new File(path.toString());
                    try  {
                        Document doc = new Document();
                        String c = t.parseToString(f);
                        doc.add(new Field("text", c, TextField.TYPE_STORED));
                        doc.add(new Field("filename", path.toString(), StringField.TYPE_STORED));
                        writer.addDocument(doc);
                    } catch (TikaException e) {
                        System.out.println("Couldn't index: " + path + " reason: " + e.getMessage());
                    }
                    System.out.println("Finished indexing: " + path + " in " + ((new Date().getTime()) - d.getTime()) + "ms");
                    return FileVisitResult.CONTINUE;
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
            Directory index = FSDirectory.open(indexPath);
            Analyzer a = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(a);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(index, iwc);
            findDocs(p, indexPath, writer);
            writer.close();

            DirectoryReader reader = DirectoryReader.open(index);
            System.out.println(reader.numDocs());
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("text", a);
            Query q = parser.parse("final");
            ScoreDoc[] hits = searcher.search(q, null, 1000).scoreDocs;
            System.out.println(hits.length);
            for (ScoreDoc s : hits) {
                Document d = searcher.doc(s.doc);
                System.out.println("found: " + d.get("filename"));
            }
        } catch (ParseException | IOException e) {
            System.out.println("Couldn't build index: " + e.getMessage());
        }
    }
}
