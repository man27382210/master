package tools.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import item.Patent;
import item.Patents;
import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.lucene.Lucene;
import tools.measure.MoehrleNovelty;
import tools.sim.PatentMatrixGenerator;
import tools.sim.Similarity;

public class LSA implements Similarity {
  // A = U X S X VT
  // A* = U X S* X VT , S* is dim-reduced from S

  private Directory index;
  private int D;
  private int W;
  private RealMatrix A, U, S, VT;
  private Map<String, Integer> docMap = new HashMap<String, Integer>();
  private Map<String, Integer> termMap = new HashMap<String, Integer>();

  public LSA(Directory index) throws IOException {
    this.index = index;
  }

  public void doSVD() {
    SingularValueDecomposition svd = new SingularValueDecomposition(A);
    S = svd.getS();
    U = svd.getU();
    VT = svd.getVT();
    reduceDim(S, 5);
    A = U.multiply(S).multiply(VT);

  }

  public void reduceDim(RealMatrix matrix, int topK) {
    int row = matrix.getRowDimension();
    int column = matrix.getColumnDimension();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < column; j++) {
        if (i > topK - 1)
          matrix.setEntry(i, j, 0);
      }
    }
  }

  public void init() throws IOException {
    IndexReader reader = DirectoryReader.open(index);

    for (int i = 0; i < reader.maxDoc(); i++) {
      Document doc = reader.document(i);
      if (doc != null)
        docMap.put(doc.get("id"), i);
    }

    Terms terms = MultiFields.getTerms(reader, "content");
    D = terms.getDocCount();
    W = (int) terms.size();
    A = new Array2DRowRealMatrix(D, W);

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
      // System.out.println(docFreq);

      long ttf = termsEnum.totalTermFreq();
      // System.out.println(ttf);

      double idf = Math.log((double) D / (double) (docFreq + 1));

      if (docFreq > minDocs && ttf > minTTF) {
        docs = termsEnum.docs(null, docs);
        int docID = -1;
        while ((docID = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
          int termFreq = docs.freq();
          if (termFreq > minTermFreq)
            A.addToEntry(docID, pos, termFreq * idf);
        }
      }
      pos++;
    }
  }

  public static void main(String[] args) throws Exception {

    // MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    Patents dataset = new Patents("dataset1", "data/dataset-3a.txt", "data/dataset-3a-answer.txt");
    Lucene nlp = new Lucene();
    nlp.load(dataset);

    LSA lsa = new LSA(nlp.getIndex());
    lsa.init();
    lsa.doSVD();

    PatentMatrixGenerator.setSimilarity(lsa);
    double d0 = System.currentTimeMillis();
    PatentMatrixGenerator.generate(dataset);
    double d1 = System.currentTimeMillis();

    MoehrleNovelty.getRanking(dataset);

    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    System.out.println("total time : " + (d1 - d0) + "ms");

    mgr.close();

  }

  public double getCosineSimiliarty(Patent p1, Patent p2) {
    String id1 = p1.getString("patent_id");
    String id2 = p2.getString("patent_id");
    RealVector v1 = A.getRowVector(docMap.get(id1));
    RealVector v2 = A.getRowVector(docMap.get(id2));
    double value = v1.cosine(v2);
    System.out.println("cosine-sim between " + id1 + " and " + id2 + " : " + value);
    return value;
  }

  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return getCosineSimiliarty(p1, p2);
  }
}
