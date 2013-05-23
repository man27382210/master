package tools.model;

import java.util.ArrayList;
import java.util.List;

import item.Patents;
import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.measure.MoehrleNovelty;
import tools.model.Lucene.WeightType;
import tools.sim.PatentMatrixGenerator;
import tools.sim.Similarity;

public class Test {
  public static void main(String[] args) throws Exception {

    // MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();

    Patents dataset = new Patents("dataset1", "data/dataset-2a.txt", "data/dataset-2a-answer.txt");
    
    LSA lsa = new LSA(new Lucene(dataset, WeightType.TFIDF));
    lsa.doSVD(16);
    
    PLSA plsa = new PLSA(new Lucene(dataset, WeightType.TF));
    plsa.doPLSA(16, 60, 10);
    
    List<Similarity> list = new ArrayList<Similarity>();
    list.add(lsa);
    list.add(plsa);
    
    PatentMatrixGenerator.setSimilarity(list);
    PatentMatrixGenerator.generate(dataset);
    MoehrleNovelty.getRanking(dataset);
    System.out.println(dataset.loadRank());
    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
  
    mgr.close();
  }
}
