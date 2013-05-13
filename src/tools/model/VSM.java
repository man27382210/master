package tools.model;

import item.Patent;
import item.Patents;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import main.MainController;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SparseRealVector;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.lucene.Lucene;
import tools.measure.MoehrleNovelty;
import tools.sim.PatentMatrixGenerator;
import tools.sim.PhraseWNSimilarity;
import tools.sim.Similarity;
import util.MakeInstrumentationUtil;

public class VSM implements Similarity {
  private Directory index;
  private int D;
  private int W;
  private Array2DRowRealMatrix matrix;
  private Map<String, Integer> docMap = new HashMap<String, Integer>();
  private Map<String, Integer> termMap = new HashMap<String, Integer>();

  public VSM(Directory index) throws IOException {
    this.index = index;
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
    matrix = new Array2DRowRealMatrix(D, W);

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

      double idf = Math.log((double) D / (double) (docFreq+1));

      if (docFreq > minDocs && ttf > minTTF) {
        docs = termsEnum.docs(null, docs);
        int docID = -1;
        while ((docID = docs.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
          int termFreq = docs.freq();
          if (termFreq > minTermFreq)
            matrix.addToEntry(docID, pos, termFreq * idf);
        }
      }
      pos++;
    }
  }

  public double getCosineSimiliarty(Patent p1, Patent p2) {
    String id1 = p1.getString("patent_id");
    String id2 = p2.getString("patent_id");
    RealVector v1 = matrix.getRowVector(docMap.get(id1));
    RealVector v2 = matrix.getRowVector(docMap.get(id2));
    double value = v1.cosine(v2);
    System.out.println("cosine-sim between " + id1 + " and " + id2 + " : " + value);
    return value;
  }

  public static void main(String[] args) throws Exception {

    // MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    Patents dataset = new Patents("dataset3", "data/dataset-3a.txt", "data/dataset-3a-answer.txt");
    Lucene nlp = new Lucene();
    nlp.load(dataset);
    VSM vsm = new VSM(nlp.getIndex());
    vsm.init();

    PatentMatrixGenerator.setSimilarity(vsm);
    double d0 = System.currentTimeMillis();
    PatentMatrixGenerator.generate(dataset);
    double d1 = System.currentTimeMillis();

    MoehrleNovelty.getRanking(dataset);

    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    System.out.println("total time : " + (d1 - d0) + "ms");

    mgr.close();

  }

  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return getCosineSimiliarty(p1, p2);
  }
}
