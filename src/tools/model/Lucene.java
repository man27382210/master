package tools.model;

import item.Patent;
import item.Patents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import tools.data.DBManager;

public class Lucene {
  private Directory index;
  private IndexReader reader;
  private Map<String, Integer> docMap;
  private Map<String, Integer> termMap;

  // number of doc
  private int N;
  // number of term
  private int M;
  // N X M matrix
  private RealMatrix docTermMatrix;

  public enum WeightType {
    TF, TFIDF
  }

  public Lucene(Patents patents, WeightType type) throws IOException {
    docMap = new HashMap<String, Integer>();
    termMap = new HashMap<String, Integer>();
    load(patents);
    reader = DirectoryReader.open(index);
    setDocMapping();
    setTermMapping();
    setMatrix(type);
  }

 

  private void setDocMapping() throws IOException {
    for (int i = 0; i < reader.maxDoc(); i++) {
      Document doc = reader.document(i);
      if (doc != null)
        docMap.put(doc.get("id"), i);
    }
    N = docMap.size();
  }

  private void setTermMapping() throws OutOfRangeException, IOException {
    Terms terms = MultiFields.getTerms(reader, "content");
    TermsEnum termsEnum = terms.iterator(null);
    BytesRef term = null;
    int pos = 0;
    while ((term = termsEnum.next()) != null) {
      termMap.put(term.utf8ToString(), pos++);
    }
    M = termMap.size();
  }

  private void setMatrix(WeightType type) throws IOException {
    docTermMatrix = new Array2DRowRealMatrix(N, M);

    Terms terms = MultiFields.getTerms(reader, "content");
    TermsEnum termsEnum = terms.iterator(null);
    BytesRef term = null;

    DocsEnum docs = null;
    int minDocs = 0;
    long minTTF = 0;
    int minTermFreq = 0;
    int pos = 0;
    while ((term = termsEnum.next()) != null) {
      termMap.put(term.utf8ToString(), pos);
      int docFreq = termsEnum.docFreq();
      long ttf = termsEnum.totalTermFreq();
      System.out.println(term.utf8ToString() + " " + pos + " " + ttf);
      double idf = Math.log((double) N / (double) (docFreq + 1));

      if (docFreq > minDocs && ttf > minTTF) {
        docs = termsEnum.docs(null, docs);
        int docID = -1;
        while ((docID = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
          int termFreq = docs.freq();
          if (termFreq > minTermFreq)
            if (type == WeightType.TF)
              docTermMatrix.addToEntry(docID, pos, termFreq);
            else if (type == WeightType.TFIDF)
              docTermMatrix.addToEntry(docID, pos, termFreq * idf);
        }
      }
      pos++;
    }
  }

  public void load(List<Patent> list) throws IOException {
    MyAnalyzer analyzer = new MyAnalyzer(Version.LUCENE_42);
    //StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
    index = new RAMDirectory();
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    IndexWriter w = new IndexWriter(index, config);

    for (Patent p : list) {
      String id = p.getString("patent_id");
      String content = p.getString("abstract") + p.getString("claims") + p.getString("description");
      addDoc(w, id, content);
    }
    w.close();
  }

  private void addDoc(IndexWriter w, String id, String content) throws IOException {
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

  public Directory getIndex() {
    return index;
  }

  public void setIndex(Directory index) {
    this.index = index;
  }

  public IndexReader getReader() {
    return reader;
  }

  public void setReader(IndexReader reader) {
    this.reader = reader;
  }

  public Map<String, Integer> getDocMap() {
    return docMap;
  }

  public void setDocMap(Map<String, Integer> docMap) {
    this.docMap = docMap;
  }

  public Map<String, Integer> getTermMap() {
    return termMap;
  }

  public void setTermMap(Map<String, Integer> termMap) {
    this.termMap = termMap;
  }

  public int getN() {
    return N;
  }

  public void setN(int n) {
    N = n;
  }

  public int getM() {
    return M;
  }

  public void setM(int m) {
    M = m;
  }

  public RealMatrix getDocTermMatrix() {
    return docTermMatrix;
  }

  public void setDocTermMatrix(RealMatrix docTermMatrix) {
    this.docTermMatrix = docTermMatrix;
  }

  public class MyAnalyzer extends Analyzer {

    private Version matchVersion;

    public MyAnalyzer(Version matchVersion) {
      this.matchVersion = matchVersion;
    }

    private CharArraySet loadStopWord() {
      CharArraySet cas = null;
      try {
        BufferedReader br = new BufferedReader(new FileReader("doc/stopword.txt"));
        cas = WordlistLoader.getWordSet(br, Version.LUCENE_42);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return cas;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
      final Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);
      TokenStream result = new MyFilter(true, source);
      result = new LengthFilter(true, result, 3, Integer.MAX_VALUE);
      result = new LowerCaseFilter(matchVersion, result);
      result = new KStemFilter(result);
      result = new StopFilter(matchVersion, result, loadStopWord());
      return new TokenStreamComponents(source, result);
    }

    public final class MyFilter extends FilteringTokenFilter {
      private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

      public MyFilter(boolean enablePositionIncrements, TokenStream in) {
        super(enablePositionIncrements, in);

      }

      @Override
      public boolean accept() throws IOException {
        return termAtt.toString().matches("^[a-zA-Z]+$");
      }
    }
  }
}
