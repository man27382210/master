package tools.lucene;

import item.PatentFullText;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Paginator;

import tools.data.DBManager;
import util.MakeInstrumentationUtil;

public class LuceneUtil {

	private Directory directory = null;

	private static void addDoc(IndexWriter w, String id, String text)
			throws IOException {
		FieldType fieldType = new FieldType();
		fieldType.setStored(true);
		fieldType.setStoreTermVectors(true);
		fieldType.setStoreTermVectorPositions(true);
		
		fieldType.setIndexed(true);
		//fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
		Document doc = new Document();
		doc.add(new StringField("id", id, Field.Store.YES));
		doc.add(new Field("text", text, fieldType));
		w.addDocument(doc);
	}

	public static void createIndex() throws IOException, ParseException {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();

		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = FSDirectory.open(new File("G:/lucene/index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		w.deleteAll();

		System.out.println(mgr.count("uspto"));

		long count = 0;
		long max = 4086265;
		long d0 = System.currentTimeMillis();
		for (int i = 0; i < 1363; i++) {
			long d1 = System.currentTimeMillis();
			List<PatentFullText> list = PatentFullText
					.findBySQL("Select * From uspto Where auto_id >= (Select auto_id From uspto Order By auto_id limit "
							+ 3000 * i + ",1) limit 3000");

			for (PatentFullText d : list) {
				String id = d.getString("patent_id");
				String text = d.getString("abstract") + d.getString("claims")
						+ d.getString("description");
				addDoc(w, id, text);
				count++;
			}
			long d2 = System.currentTimeMillis();
			System.out.println("query & index time : " + (d2 - d1) + " ms");
			System.out.println("total doc : " + count + " done : "
					+ (double) (count) / (double) (max));
		}

		w.close();
		mgr.close();

	}

	private static void query() throws ParseException, IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = FSDirectory.open(new File("G:/lucene/index"));

		// 2. query
		String querystr = "process";

		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_42, "text", analyzer)
				.parse(querystr);

		// 3. search
		int hitsPerPage = 10000000;
		IndexReader reader = DirectoryReader.open(index);
		
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopDocs topDocs = searcher.search(q, 10);
		int totalHits = topDocs.totalHits;
		System.out.println("Found " + totalHits + " hits.");
		
		String sentence = "present invention";
        PhraseQuery query = new PhraseQuery();
        String[] words = sentence.split(" ");
        for (String word : words) {
            query.add(new Term("text", word));
        }
        
        topDocs = searcher.search(query, 10);
		totalHits = topDocs.totalHits;
		System.out.println("Found " + totalHits + " hits.");
		
//		TopScoreDocCollector collector = TopScoreDocCollector.create(
//				hitsPerPage, true);
//		searcher.search(q, collector);
//		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		//System.out.println("Found " + hits.length + " hits.");
		// for (int i = 0; i < hits.length; ++i) {
		// int docId = hits[i].doc;
		// Document d = searcher.doc(docId);
		// System.out.println((i + 1) + ". " + d.get("id"));
		// }
	}

	public static void main(String[] args) {
		try {
			LuceneUtil.createIndex();
//			long d0 = System.currentTimeMillis();
//			LuceneUtil.query();
//			long d1 = System.currentTimeMillis();
//			System.out.println("query:" + (d1 - d0) + "ms");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}