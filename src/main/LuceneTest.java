package main;

import item.Patent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import tools.data.FileManager;

public class LuceneTest {
	
	
	
	private static void addDoc(IndexWriter w, String title, String isbn)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.NO));
		doc.add(new StringField("isbn", isbn, Field.Store.NO));
		w.addDocument(doc);
	}

	public static void main(String[] args) throws IOException {

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Lucene in Action", "193398817");
		addDoc(w, "Lucene for Dummies Lucene", "55320055Z");
		addDoc(w, "Managing Gigabytes singal", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");
		
		FileManager mgr = new FileManager();
		List<Patent> list = (List<Patent>) mgr
		    .readObjectFromFile("data/dataset1.txt");
		for (Patent p : list) {
			addDoc(w, p.getFullText(), p.getId());
		}
		
		w.close();
	
		IndexReader reader = DirectoryReader.open(index);
		TermsEnum terms = MultiFields.getTerms(reader, "title").iterator(null);
		BytesRef term = null;
		DocsEnum docs = null;
		int minDocs = 0;
		long minTTF = 0;
		int minTermFreq = 0;
		
		do {
      term = terms.next();
      if (term != null) {
        int docFreq = terms.docFreq();
        long ttf = terms.totalTermFreq();
        if (docFreq > minDocs && ttf > minTTF) {
          docs = terms.docs(null, docs);
          int docID = -1;
          do {
            docID = docs.nextDoc();
            if (docID != DocIdSetIterator.NO_MORE_DOCS) {
              int termFreq = docs.freq();
              if (termFreq > minTermFreq)
              	System.out.println(term.utf8ToString() + "," + docID + "," + docs.freq());
            }
          } while (docID != DocIdSetIterator.NO_MORE_DOCS);
        }
      }
    } while (term != null);
	}
	
	
	
	public static int getTF(IndexReader reader, String term, String field, int docID) throws IOException
	{
	    Terms termVector = reader.getTermVector(docID, field);
	    TermsEnum termsEnum = termVector.iterator(null);
	    Map<String, Integer> frequencies = new HashMap<String, Integer>();
	    BytesRef text = null;
	    while ((text = termsEnum.next()) != null) {
	        String s = text.utf8ToString();
	        int freq = (int) termsEnum.totalTermFreq();
	        frequencies.put(s, freq);
	    }
	    return frequencies.get(term);
	}
	
	
	private static void s(Object o) {
		System.out.println(o);
	}
	
	private static Map<String, Integer> getTermFrequencies(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, "title");
        System.out.println(vector.getDocCount());
//        vector.iterator(null);
//        TermsEnum termsEnum = null;
//        termsEnum = vector.iterator(termsEnum);
//        Map<String, Integer> frequencies = new HashMap<String, Integer>();
//        BytesRef text = null;
//        while ((text = termsEnum.next()) != null) {
//            String term = text.utf8ToString();
//            int freq = (int) termsEnum.totalTermFreq();
//            frequencies.put(term, freq);
//        }
//        return frequencies;
        return null;
    }
}
