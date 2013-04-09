package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import item.Patent;
import item.SaoTuple;

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

	public static void main(String[] args) throws IOException, ParseException {
    Directory directory = new RAMDirectory();  
    Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_42);
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    IndexWriter writer = new IndexWriter(directory, config);
    addDoc(writer, "1123","bla bla bla bleu bleu");
    addDoc(writer, "1234","bla bla bla bla");
    writer.close();
    DirectoryReader reader = DirectoryReader.open(directory);
    DocsEnum de = MultiFields.getTermDocsEnum(reader, MultiFields.getLiveDocs(reader), "content", new BytesRef("bla"));
    int doc;
    while((doc = de.nextDoc()) != DocsEnum.NO_MORE_DOCS) {
          System.out.println(de.freq());
    }
    reader.close();
}
	
	private static void addDoc(IndexWriter w, String id, String content)
	    throws IOException {
		FieldType fieldType = new FieldType();
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setIndexed(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    fieldType.setStored(true);
    
		Document doc = new Document();
		doc.add(new StringField("id", id, Field.Store.YES));
		doc.add(new Field("content", content, fieldType));
		//doc.add(new TextField("content", content, Field.Store.NO));
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
			String id = p.getString("patent_id");
			String content = p.getString("abstract") + p.getString("claims") + p.getString("description");
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

		while((term = terms.next()) != null) {
			// map (doc id, term freq)
			HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
			int docFreq = terms.docFreq();
			long ttf = terms.totalTermFreq();
			if (docFreq > minDocs && ttf > minTTF) {
				docs = terms.docs(null, docs);
				int docID = -1;
				while((docID = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
					int termFreq = docs.freq();
					if (termFreq > minTermFreq)
						map.put(docID, docs.freq());
				}
				termFreqMap.put(term.utf8ToString(), map);
			}
		}
	}

	private int getTF(String id, String term) {
		int docID = idMap.get(id);
		Map<Integer, Integer> map = termFreqMap.get(term);
		if (map == null || !map.containsKey(docID)) return 0;
		int termFreq = map.get(docID);
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
	
	public double getTFIDF(String id, SaoTuple t) {
		double value = getTFIDF(id, t.getSubject())
				+ getTFIDF(id, t.getPredicate())
				+ getTFIDF(id, t.getObject());
		System.out.println(t.getSubject() + " TFIDF:" + getTFIDF(id, t.getSubject()));
		return value;
	}
}
