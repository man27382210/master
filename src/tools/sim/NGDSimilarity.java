package tools.sim;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

import tools.lucene.LuceneUtil;
import tools.nlp.StanfordUtil;
import tools.sim.WNSimilarity.Pair;

import item.Patent;
import item.SAO;

public class NGDSimilarity implements SAOBasedSimilarity {
  private static NGDSimilarity instance = null;
  private LuceneUtil lucene = null;
  private static Map<Pair<String, String>, Double> cache = new HashMap<Pair<String, String>, Double>();
  private static Map<String, Integer> one_word_cache = new HashMap<String, Integer>();
  private static Map<Pair<String, String>, Integer> two_word_cache = new HashMap<Pair<String, String>, Integer>();

  public static NGDSimilarity getInstance() throws IOException {
    if (instance == null) {
      instance = new NGDSimilarity();
      instance.lucene = LuceneUtil.getInstance();
    }
    return instance;
  }

  public NGDSimilarity() throws IOException {
    lucene = LuceneUtil.getInstance();
  }
  
  @Override
  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException {
    List<SAO> l1 = p1.getSaoTupleList();
    List<SAO> l2 = p2.getSaoTupleList();

    double[][] sim = new double[l1.size()][l2.size()];

    for (int i = 0; i < l1.size(); i++) {
      for (int j = 0; j < l2.size(); j++) {
        sim[i][j] = saoSim(l1.get(i), l2.get(j));
      }
    }

    double sum_of_i_to_j = 0;
    for (int i = 0; i < l1.size(); i++) {
      double max = 0;
      for (int j = 0; j < l2.size(); j++) {
        if (sim[i][j] > max)
          max = sim[i][j];
      }
      sum_of_i_to_j += max;
    }

    double sum_of_j_to_i = 0;
    for (int j = 0; j < l2.size(); j++) {
      double max = 0;
      for (int i = 0; i < l1.size(); i++) {
        if (sim[i][j] > max)
          max = sim[i][j];
      }
      sum_of_j_to_i += max;
    }

    return (sum_of_i_to_j / l1.size() + sum_of_j_to_i / l2.size()) / 2;

  }

  @Override
  public double saoSim(SAO t1, SAO t2) throws ParseException, IOException, InterruptedException {
    double s1 = pharseSim(t1.getString("subject"), t2.getString("subject"));
    double s2 = pharseSim(t1.getString("predicate"), t2.getString("predicate"));
    double s3 = pharseSim(t1.getString("object"), t2.getString("object"));
    double s4 = pharseSim(t1.getString("subject"), t2.getString("object"));
    double s5 = pharseSim(t1.getString("object"), t2.getString("subject"));
    return (s1 + s2 + s3) / 3;
    //return (s1 + s2 + s3 + s4 + s5) / 5;
  }

  @Override
  public double pharseSim(String pharse1, String pharse2) throws ParseException, IOException, InterruptedException {
    String[] l1 = pharse1.split(" ");
    String[] l2 = pharse2.split(" ");

    double[][] sim = new double[l1.length][l2.length];

    for (int i = 0; i < l1.length; i++) {
      for (int j = 0; j < l2.length; j++) {
        sim[i][j] = wordSim(l1[i], l2[j]);
      }
    }

    double sum_of_i_to_j = 0;
    for (int i = 0; i < l1.length; i++) {
      double max = 0;
      for (int j = 0; j < l2.length; j++) {
        if (sim[i][j] > max)
          max = sim[i][j];
      }
      sum_of_i_to_j += max;
    }

    double sum_of_j_to_i = 0;
    for (int j = 0; j < l2.length; j++) {
      double max = 0;
      for (int i = 0; i < l1.length; i++) {
        if (sim[i][j] > max)
          max = sim[i][j];
      }
      sum_of_j_to_i += max;
    }

    return (sum_of_i_to_j / l1.length + sum_of_j_to_i / l2.length) / 2;
  }

  @Override
  public double wordSim(String word1, String word2) throws ParseException, IOException, InterruptedException {
    Pair<String, String> ngd_pair = new Pair<String, String>(word1, word2);
    if (cache.containsKey(ngd_pair))
      return cache.get(ngd_pair);

    System.out.println(word1 + " " + word2);

    int countX = 0, countY = 0, countXY = 0;

    if (one_word_cache.containsKey(word1)) {
      countX = one_word_cache.get(word1);
    } else {
      countX = lucene.queryCount(word1);
      one_word_cache.put(word1, countX);
    }
    
    if (one_word_cache.containsKey(word2)) {
      countY = one_word_cache.get(word2);
    } else {
      countY = lucene.queryCount(word2);
      one_word_cache.put(word2, countY);
    }
    
    Pair<String, String> pair = new Pair<String, String>(word1, word2);
    if (two_word_cache.containsKey(pair)) {
      countXY = two_word_cache.get(pair);
    } else {
      countXY = lucene.queryCount(word1, word2);
      two_word_cache.put(pair, countXY);
    }

    double sim = 1 - getNGD(countX, countY, countXY);
    cache.put(pair, sim);
    return getNGD(countX, countY, countXY);
  }

  public double getNGD(int countX, int countY, int countXY) throws IOException, InterruptedException {
    int M = 4086265;
    double LogOfXhits = Math.log(countX);
    double LogOfYhits = Math.log(countY);
    double LogOfXYhits = Math.log(countXY);
    double LogOfM = Math.log(M);

    double distance = (Math.max(LogOfXhits, LogOfYhits) - LogOfXYhits) / (LogOfM - Math.min(LogOfXhits, LogOfYhits));

    if (distance < 0) {
      distance = 0;
    } else if (distance > 1) {
      distance = 1;
    }
    return distance;
  }

  public static void main(String[] args) {
    NGDSimilarity sim;
    try {
      sim = NGDSimilarity.getInstance();
      System.out.println(sim.pharseSim("device", "device"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
