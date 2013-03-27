package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class LuceneTest {
	private static void addDoc(IndexWriter w, String title, String isbn)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));

		// use a string field for isbn because we don't want it tokenized
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		w.addDocument(doc);
	}

	public static void main(String[] args) throws IOException {

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Lucene in Action", "193398817");
		addDoc(w, "Lucene for Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");
		w.close();

		
	
		System.out.println(index.toString());
		
		
		IndexReader reader = DirectoryReader.open(index);
		Fields fields = reader.getTermVectors(0);
		System.out.println(fields);
		for(String field : fields) {
			System.out.println(field);
			Terms terms = fields.terms(field);
		}
		for (int i = 0; i < reader.maxDoc(); i++) {
			
			System.out.println(reader.docFreq(new Term("title")));	
			//Map<String, Integer> map = getTermFrequencies(reader, i);
			//System.out.println(map);
		}

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
