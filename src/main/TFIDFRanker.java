package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.Fields;
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
	// map (doc id, total term num)
	Map<Integer, Integer> docLengthMap = new HashMap<Integer,Integer>();
	
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
		w.addDocument(doc);
	}

	private void setIdMap(IndexReader reader) throws IOException {
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document doc = reader.document(i);
			// System.out.println(doc.get("id"));
			if (doc != null)
				idMap.put(doc.get("id"), i);
		}
		// System.out.println(idMap.size());
	}

	public static void main(String[] args){
		TFIDFRanker r = TFIDFRanker.getInstance();
		try {
	    r.test();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
	
	private void test() throws IOException{
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
		    analyzer);
		IndexWriter w = new IndexWriter(index, config);

		addDoc(w, "p01", "I love young man.");
		addDoc(w, "p02", "you love young girl.");
		addDoc(w, "p03", "I dont love young man, and I love you.");
		
		
		w.close();
		IndexReader reader = DirectoryReader.open(index);
		setIdMap(reader);
		setTermFreqMap(reader);
		setDocLengthMap();
		
		FieldInvertState fis = new FieldInvertState("content"); 
		System.out.println(fis.getLength());
		
		System.out.println(idMap.toString());
		System.out.println(termFreqMap.toString());
		System.out.println(docLengthMap.toString());
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
		IndexReader reader = DirectoryReader.open(index);
		setIdMap(reader);
		setTermFreqMap(reader);
		setDocLengthMap();
	}

	private void setTermFreqMap(IndexReader reader) throws IOException {
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
	
	private void setDocLengthMap() throws IOException {
		Iterator<Entry<String, HashMap<Integer, Integer>>> i1 = termFreqMap.entrySet().iterator();
		
		while (i1.hasNext()) {
			Entry e1 =  i1.next();   
      Map map = (Map) e1.getValue();
      Iterator i2 = map.entrySet().iterator();
      while (i2.hasNext()) {
      	Entry e2 = (Entry) i2.next();
      	int docID = (Integer) e2.getKey();
      	int num = (Integer) e2.getValue();
      	if (docLengthMap.get(docID) != null) {
      		docLengthMap.put(docID, docLengthMap.get(docID) + num);
      	} else {
      		docLengthMap.put(docID, num);
      	}
      }
		}
	}
	
	private double getTF(String id, String term) {
		int docID = idMap.get(id);
		Map<Integer, Integer> map = termFreqMap.get(term);
		if (map == null || !map.containsKey(docID)) return 0;
		int termFreq = map.get(docID);
		int docLength = docLengthMap.get(docID);
		//System.out.println("tf:" + termFreq+ " pid:" + id + " term:" + term + " dl:" + docLength);
		return (double)termFreq / (double)docLength;
	}

	private double getIDF(String term) {
		int total = idMap.size();
		Map<Integer, Integer> map = termFreqMap.get(term);
		if (map == null) return 0;
		int docFreq = map.size();
		return Math.log((double)total / (double)docFreq);
	}

	public double getTFIDF(String id, String term) {
		return getTF(id, term) * getIDF(term);
	}
	
	public double getTFIDF(String id, SaoTuple t) {
		double value = getTFIDF(id, t.getString("subject"))
				+ getTFIDF(id, t.getString("predicate"))
				+ getTFIDF(id, t.getString("object"));
		//System.out.println(t.getString("subject") + " TFIDF:" + getTFIDF(id, t.getString("subject")));
		return value;
	}
}
