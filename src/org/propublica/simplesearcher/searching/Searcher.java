package org.propublica.simplesearcher.searching;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;

public final class Searcher {
    private Searcher() {
    }

    public static ArrayList<Document> search(String term, DirectoryReader directoryReader) throws ParseException, IOException {
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        Analyzer a = new StandardAnalyzer();
        QueryParser parser = new QueryParser("text", a);
        Query q = parser.parse(term);
        ScoreDoc[] hits = searcher.search(q, null, 1000).scoreDocs;
        ArrayList<Document> arrayList = new ArrayList<>();
        for (ScoreDoc hit : hits)
            arrayList.add(searcher.doc(hit.doc));
        return arrayList;
    }
}
