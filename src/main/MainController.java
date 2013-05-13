package main;

import item.Patent;
import item.Patents;
import item.SAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tools.data.DBManager;
import tools.data.DataSetLoader;
import tools.evaluation.AUC;
import tools.evaluation.PRCurve;
import tools.measure.MoehrleNovelty;
import tools.sim.MoehrleWNSimilarity;
import tools.sim.NGDSimilarity;
import tools.sim.PatentMatrixGenerator;
import tools.sim.PhraseWNSimilarity;
import tools.sim.RandomSimilarity;
import tools.sim.Similarity;
import tools.sim.WNSimilarity;
import util.MakeInstrumentationUtil;
import weka.core.converters.DatabaseLoader;

public class MainController {

  public static void main(String[] args) {
    try {
      MakeInstrumentationUtil.make();
      DBManager mgr = DBManager.getInstance();
      mgr.open();
      MainController.run();
      mgr.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void run() throws Exception {
    int topK = 5;
    
    Patents dataset = new Patents("dataset1" , "data/dataset-1a.txt" , "data/dataset-1a-answer.txt");
    SAOPreprocessor.parseTree(dataset);
    SAOExtractor.extract(dataset);
    SAOFilter.filter(dataset);

    TFIDFFilter filter = TFIDFFilter.getInstance();
    filter.filter(dataset, topK);
  
    // 5. generate dissimilarity matrix
    Similarity sim = new PhraseWNSimilarity();
    PatentMatrixGenerator.setSimilarity(sim);
    
    double d0 = System.currentTimeMillis();
    PatentMatrixGenerator.generate(dataset);
    double d1 = System.currentTimeMillis();
    
    MoehrleNovelty.getRanking(dataset);

    PRCurve.evaluate(dataset);
    AUC.evaluate(dataset);
    System.out.println("topK : " + topK + " total time : " + (d1-d0) + "ms");
  }
  
}