package tools.model;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import tools.data.DBManager;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.measure.MoehrleNovelty;
import tools.sim.PatentMatrixGenerator;
import tools.sim.WNSimilarity;
import util.MakeInstrumentationUtil;

import main.MainController;
import main.SAOExtractor;
import main.SAOFilter;
import main.SAOPreprocessor;
import main.TFIDFFilter;
import item.Patents;

public class SAOWN {
  private int topK = 3;
  private Patents dataset;

  public SAOWN(Patents dataset) {
    this.dataset = dataset;
  }

  public SAOWN(Patents dataset, int topK) {
    this.dataset = dataset;
    this.topK = topK;
  }

  public void run() throws Exception {
    SAOPreprocessor.parseTree(dataset);
    SAOExtractor.extract(dataset);
    SAOFilter.filter(dataset);
    TFIDFFilter.filter(dataset, topK);
    PatentMatrixGenerator.setSimilarity(new WNSimilarity());
    PatentMatrixGenerator.generate(dataset);
    MoehrleNovelty.getRanking(dataset);
    System.out.println(dataset.loadRank());
    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    System.out.println(WNSimilarity.ZERO);
    System.out.println(WNSimilarity.NON_ZERO);
  }

  public static void main(String[] args) {
    try {
      MakeInstrumentationUtil.make();
      DBManager mgr = DBManager.getInstance();
      mgr.open();
      Patents dataset = new Patents("dataset5", "data/dataset-7a.txt", "data/dataset-7a-answer.txt");
      //Patents dataset = new Patents("dataset3", "data/dataset-test.txt", "data/dataset-test.txt");
      SAOWN method = new SAOWN(dataset, 5);
      method.run();
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
