package tools.model;

import item.Patent;
import item.Patents;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.queryparser.classic.ParseException;
import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.model.Lucene;
import tools.model.Lucene.WeightType;
import tools.measure.MoehrleNovelty;
import tools.sim.PatentMatrixGenerator;
import tools.sim.Similarity;

public class VSM implements Similarity {
  private RealMatrix N;
  private Map<String, Integer> docMap = new HashMap<String, Integer>();

  public VSM(Lucene tool) throws IOException {
    N = tool.getDocTermMatrix();
    docMap = tool.getDocMap();
  }

  public double getCosineSimiliarty(Patent p1, Patent p2) {
    String id1 = p1.getString("patent_id");
    String id2 = p2.getString("patent_id");
    RealVector v1 = N.getRowVector(docMap.get(id1));
    RealVector v2 = N.getRowVector(docMap.get(id2));
    double value = v1.cosine(v2);
    System.out.println("cosine-sim between " + id1 + " and " + id2 + " : " + value);
    return value;
  }

  public static void main(String[] args) throws Exception {

    // MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    Patents dataset = new Patents("dataset1", "data/dataset-4a.txt", "data/dataset-4a-answer.txt");
    Lucene nlp = new Lucene(dataset,  WeightType.TFIDF);
    VSM vsm = new VSM(nlp);
    PatentMatrixGenerator.setSimilarity(vsm);
    PatentMatrixGenerator.generate(dataset);
    MoehrleNovelty.getRanking(dataset);
    dataset.getInfo();
    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    mgr.close();

  }

  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return getCosineSimiliarty(p1, p2);
  }
}
