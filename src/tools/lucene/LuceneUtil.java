package tools.lucene;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Paginator;

import core.dbmodel.MakeInstrumentationUtil;
import core.dbmodel.PatentFullText;
import core.similarity.WNSimilarity;

import tools.data.DBManager;

public class LuceneUtil {
  private static LuceneUtil instance = null;
  private static StandardAnalyzer analyzer;
  private static Directory index;
  private static DirectoryReader reader;
  private static IndexSearcher searcher;

  public static LuceneUtil getInstance() throws IOException {
    if (instance == null) {
      instance = new LuceneUtil();
      instance.openIndex();
    }
    return instance;
  }

  private static void addDoc(IndexWriter w, String id, String content)
      throws IOException {
    FieldType fieldType = new FieldType();
    fieldType.setStoreTermVectors(true);
    fieldType.setStoreTermVectorPositions(true);
    fieldType.setIndexed(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    fieldType.setStored(true);
    
    Document doc = new Document();
    doc.add(new StringField("id", id, Field.Store.YES));
    doc.add(new Field("content", content, fieldType));
    w.addDocument(doc);
  }
  
  
  public void createIndex() throws IOException, ParseException {
    MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
    Directory index = FSDirectory.open(new File("G:/lucene/index"));
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    IndexWriter w = new IndexWriter(index, config);
    w.deleteAll();

    System.out.println(mgr.count("uspto"));

    long count = 0;
    long max = 4086265;
    for (int i = 0; i < 1363; i++) {
      long d1 = System.currentTimeMillis();
      List<PatentFullText> list = PatentFullText.findBySQL("Select * From uspto Where auto_id >= (Select auto_id From uspto Order By auto_id limit " + 3000 * i + ",1) limit 3000");

      for (PatentFullText d : list) {
        String id = d.getString("patent_id");
        String text = d.getString("abstract") + d.getString("claims") + d.getString("description");
        addDoc(w, id, text);
        count++;
      }
      long d2 = System.currentTimeMillis();
      System.out.println("query & index time : " + (d2 - d1) + " ms");
      System.out.println("total doc : " + count + " done : " + (double) (count) / (double) (max));
    }

    w.close();
    mgr.close();

  }

  public void openIndex() throws IOException {
    analyzer = new StandardAnalyzer(Version.LUCENE_42);
    index = FSDirectory.open(new File("G:/lucene/index"));
    reader = DirectoryReader.open(index);
    searcher = new IndexSearcher(reader);
  }

  public int queryCount(String queryStr) throws ParseException, IOException {
    TotalHitCountCollector collector = new TotalHitCountCollector();
    PhraseQuery query = new PhraseQuery();
    String[] words = queryStr.split(" ");
    for (String word : words) {
      query.add(new Term("text", word));
    }
    long d0 = System.currentTimeMillis();
    searcher.search(query, collector);
    long d1 = System.currentTimeMillis();
    System.out.println("query:" + queryStr + " time:" + (d1 - d0) + "ms");
    return collector.totalHits;
  }

  public int queryCount(String queryStr1 ,String queryStr2) throws ParseException, IOException {
    TotalHitCountCollector collector = new TotalHitCountCollector();
    PhraseQuery query1 = new PhraseQuery();
    String[] words1 = queryStr1.split(" ");
    for (String word : words1) {
      query1.add(new Term("text", word));
    }
    
    PhraseQuery query2 = new PhraseQuery();
    String[] words2 = queryStr2.split(" ");
    for (String word : words2) {
      query2.add(new Term("text", word));
    }
    
    BooleanQuery booleanQuery = new BooleanQuery();
    booleanQuery.add(query1, BooleanClause.Occur.MUST);
    booleanQuery.add(query2, BooleanClause.Occur.MUST);
    
    long d0 = System.currentTimeMillis();
    searcher.search(booleanQuery, collector);
    long d1 = System.currentTimeMillis();
    System.out.println("query:" + queryStr1 + " AND " + queryStr2 + " time:" + (d1 - d0) + "ms");
    return collector.totalHits;
  }
  

  public static void main(String[] args) {
    try {
      // LuceneUtil.createIndex();
      LuceneUtil l = LuceneUtil.getInstance();
      System.out.println(l.queryCount("shift"));
      System.out.println(l.queryCount("trasfer"));
      System.out.println(l.queryCount("clamp","blade"));
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static class TotalHitCountCollector extends Collector {
    private int totalHits;

    /** Returns how many hits matched the search. */
    public int getTotalHits() {
      return totalHits;
    }

    @Override
    public void collect(int doc) {
      totalHits++;
    }

    @Override
    public boolean acceptsDocsOutOfOrder() {
      return true;
    }

    @Override
    public void setScorer(Scorer arg0) throws IOException {
      // TODO Auto-generated method stub

    }

    @Override
    public void setNextReader(AtomicReaderContext arg0) throws IOException {
      // TODO Auto-generated method stub

    }
  }
}