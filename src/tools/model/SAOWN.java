package tools.model;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import core.dbmodel.MakeInstrumentationUtil;
import core.dbmodel.Patents;
import core.model.sao.SAOExtractor;
import core.model.sao.SAOFilter;
import core.model.sao.SAOPreprocessor;
import core.model.sao.TFIDFFilter;
import core.similarity.PatentMatrixGenerator;
import core.similarity.WNSimilarity;

import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.measure.MoehrleNovelty;


public class SAOWN {
  private Patents dataset;

  public SAOWN(Patents dataset) throws Exception {
    this.dataset = dataset;
    preRun();
  }

  public void preRun() throws Exception {
    SAOPreprocessor.parseTree(dataset);
    SAOExtractor.extract(dataset);
    SAOFilter.filter(dataset);
  }

  public void runByTopKSAOSelection(int topK) throws Exception {
    TFIDFFilter.filter(dataset, topK);
    PatentMatrixGenerator.setSimilarity(new WNSimilarity());
    PatentMatrixGenerator.generate(dataset);
    MoehrleNovelty.getRanking(dataset);
    // System.out.println(dataset.loadRank());
    System.out.println("=== result ===");
    System.out.println(dataset.getName() + ",SAO," + "top" + topK);
    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    System.out.println("=== result ===");
    // System.out.println(WNSimilarity.ZERO);
    // System.out.println(WNSimilarity.NON_ZERO);
  }

  public static void main(String[] args) {
    try {
      MakeInstrumentationUtil.make();
      DBManager mgr = DBManager.getInstance();
      mgr.open();
      for (int i = 1; i <= 5; i++) {
        Patents dataset = new Patents("dataset" + i, "data/dataset-" + i + ".txt", "data/dataset-" + i + "-answer.txt");
        // Patents dataset = new Patents("dataset3", "data/dataset-test.txt",
        // "data/dataset-test.txt");
        SAOWN method = new SAOWN(dataset);
        method.runByTopKSAOSelection(30);
        method.runByTopKSAOSelection(25);
        method.runByTopKSAOSelection(20);
        method.runByTopKSAOSelection(15);
        method.runByTopKSAOSelection(10);
        method.runByTopKSAOSelection(5);
        method.runByTopKSAOSelection(3);
      }

      mgr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
