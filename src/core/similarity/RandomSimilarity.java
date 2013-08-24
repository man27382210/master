package core.similarity;


import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import core.dbmodel.Patent;
import core.dbmodel.SAO;

import tools.nlp.StanfordUtil;

public class RandomSimilarity implements SAOBasedSimilarity {

  private static RandomSimilarity instance = null;

  public static RandomSimilarity getInstance() {
    if (instance == null) {
      instance = new RandomSimilarity();
    }
    return instance;
  }
  
  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    return Math.random();
  }

  @Override
  public double saoSim(SAO t1, SAO t2) throws ParseException, IOException, InterruptedException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double pharseSim(String pharse1, String pharse2) throws ParseException, IOException, InterruptedException {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double wordSim(String word1, String word2) throws ParseException, IOException, InterruptedException {
    // TODO Auto-generated method stub
    return 0;
  }
}
