package main;

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
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import tools.data.FileManager;

import item.Patent;

public class TFIDFRanker {
	private static TFIDFRanker instance = null;

	public static TFIDFRanker getInstance() {
		if (instance == null) {
			instance = new TFIDFRanker();
		}
		return instance;
	}

	// map (patent id , doc id)
	Map<String, Integer> idMap = new HashMap<String, Integer>();
	// map (term , map (doc id , termfreq) )
	Map<String, HashMap<Integer, Integer>> termFreqMap = new HashMap<String, HashMap<Integer, Integer>>();

//	public static void main(String args[]) throws IOException {
//		FileManager mgr = new FileManager();
//		List<Patent> list = (List<Patent>) mgr.readObjectFromFile("data/dataset1.txt");
//		TFIDFRanker r = new TFIDFRanker();
//		r.load(list);
//	}

	private void addDoc(IndexWriter w, String id, String content)
	    throws IOException {
		Document doc = new Document();
		doc.add(new StringField("id", id, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.NO));
		w.addDocument(doc);
	}

	private void setIdMap(Directory index) throws IOException {
		IndexReader reader = DirectoryReader.open(index);
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document doc = reader.document(i);
			// System.out.println(doc.get("id"));
			if (doc != null)
				idMap.put(doc.get("id"), i);
		}
		// System.out.println(idMap.size());
	}

	public void load(List<Patent> list) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
		    analyzer);
		IndexWriter w = new IndexWriter(index, config);

		for (Patent p : list) {
			String id = p.getId();
			String content = p.getFullText();
			addDoc(w, id, content);
		}
		w.close();
		setIdMap(index);
		setTermFreqMap(index);
	}

	private void setTermFreqMap(Directory index) throws IOException {
		IndexReader reader = DirectoryReader.open(index);
		TermsEnum terms = MultiFields.getTerms(reader, "content").iterator(null);
		BytesRef term = null;
		DocsEnum docs = null;
		int minDocs = 0;
		long minTTF = 0;
		int minTermFreq = 0;

		do {
			term = terms.next();
			// System.out.println(term.utf8ToString());
			if (term != null) {
				HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
				int docFreq = terms.docFreq();
				// System.out.println(docFreq);
				long ttf = terms.totalTermFreq();
				// System.out.println(ttf);
				if (docFreq > minDocs && ttf > minTTF) {
					docs = terms.docs(null, docs);
					int docID = -1;
					do {
						docID = docs.nextDoc();
						if (docID != DocIdSetIterator.NO_MORE_DOCS) {
							int termFreq = docs.freq();
							if (termFreq > minTermFreq)
								map.put(docID, docs.freq());
							// System.out.println(term.utf8ToString() + "," + docID + ","
							// + docs.freq());
						}
					} while (docID != DocIdSetIterator.NO_MORE_DOCS);
				}
				termFreqMap.put(term.utf8ToString(), map);
			}
		} while (term != null);
	}

	private int getTF(String id, String term) {
		int docID = idMap.get(id);
		Map<Integer, Integer> map = termFreqMap.get(term);
		if (map == null || !map.containsKey(docID)) return 0;
//		System.out.println(map);
//		System.out.println(docID);
//		System.out.println(term);
		int termFreq = map.get(docID);
		//System.out.println(term + " tf=" + termFreq);
		return termFreq;
	}

	private double getIDF(String term) {
		int total = idMap.size();
		//System.out.println("t" + total);
		Map<Integer, Integer> map = termFreqMap.get(term);
		if (map == null) return 0;
		int docFreq = map.size();
		return Math.log(total / docFreq);
	}

	public double getTFIDF(String id, String term) {
		return getTF(id, term) * getIDF(term);
	}
}
