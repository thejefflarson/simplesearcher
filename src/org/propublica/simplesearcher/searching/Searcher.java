package org.propublica.simplesearcher.searching;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.ArrayList;

public final class Searcher {
    private Searcher() {
    }

    public static ArrayList<Document> search(String term, DirectoryReader directoryReader) throws QueryNodeException, IOException {
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        Analyzer a = new StandardAnalyzer();
        StandardQueryParser parser = new StandardQueryParser(a);
        Query q = parser.parse(term, "text");
        ScoreDoc[] hits = searcher.search(q, null, 1000).scoreDocs;
        ArrayList<Document> arrayList = new ArrayList<>();

        for (ScoreDoc hit : hits) {
            arrayList.add(searcher.doc(hit.doc));
        }

        return arrayList;
    }
}
