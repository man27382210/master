package tools.model;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import org.apache.lucene.queryparser.classic.ParseException;

import core.dbmodel.MakeInstrumentationUtil;
import core.dbmodel.Patent;
import core.dbmodel.Patents;
import core.similarity.PatentMatrixGenerator;
import core.similarity.Similarity;

import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.model.Lucene;
import tools.model.Lucene.WeightType;
import tools.measure.MoehrleNovelty;

public class LSA implements Similarity {

  // N = U X S X VT
  // N* = U X S* X VT , N* is dim-reduced from N
  private RealMatrix N, U, S, VT;
  private Map<String, Integer> docMap;

  public LSA(Lucene tool) throws IOException {
    N = tool.getDocTermMatrix();
    docMap = tool.getDocMap();
  }

  public void doSVD(int dim) {
    SingularValueDecomposition svd = new SingularValueDecomposition(N);
    S = svd.getS();
    U = svd.getU();
    VT = svd.getVT();
    reduceDim(S, dim);
    N = U.multiply(S).multiply(VT);
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

  public double getCosineSimiliarty(Patent p1, Patent p2) {
    String id1 = p1.getString("patent_id");
    String id2 = p2.getString("patent_id");
    RealVector v1 = N.getRowVector(docMap.get(id1));
    RealVector v2 = N.getRowVector(docMap.get(id2));
    double value = v1.cosine(v2);
    // System.out.println("cosine-sim between " + id1 + " and " + id2 + " : " +
    // value);
    return value;
  }

  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return getCosineSimiliarty(p1, p2);
  }

  public static void main(String[] args) throws Exception {

    // MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    int[] set = { 1, 2, 3, 4, 5 };
    int[] dimSet = { 30, 25, 20, 15, 10, 5 };
    for (int i = 0; i < set.length; i++) {
      int num = set[i];
      for (int j = 0; j < dimSet.length; j++) {
        int dim = dimSet[j];
        Patents dataset = new Patents("dataset" + num, "data/dataset-" + num + ".txt", "data/dataset-" + num + "-answer.txt");
        LSA lsa = new LSA(new Lucene(dataset, WeightType.TFIDF));
        System.out.println("==========");
        System.out.println("model = lsa, dataset = " + dataset.getName() + ", k = " + dim);
        lsa.doSVD(dim);
        PatentMatrixGenerator.setSimilarity(lsa);
        PatentMatrixGenerator.generate(dataset);
        MoehrleNovelty.getRanking(dataset);
        //PRCurve.evaluate(dataset);
        AUC.evaluate(dataset);
      }
    }

    mgr.close();
  }
}
