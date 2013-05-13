package tools.sim;

import item.Patent;
import item.SAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.nlp.StanfordUtil;

import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;
import edu.sussex.nlp.jws.WuAndPalmer;

public class WNSimilarity implements SAOBasedSimilarity {
  private static StanfordUtil stanford = StanfordUtil.getInstance();
  private static boolean LEMMATIZE = true;
  private static Map<Pair<String, String>, Double> cache = new HashMap<Pair<String, String>, Double>();

  public WNSimilarity(boolean mfs, boolean cache, boolean lemma) {
    WS4JConfiguration.getInstance().setMFS(mfs);
    WS4JConfiguration.getInstance().setCache(cache);
    LEMMATIZE = lemma;
  }
  
  public WNSimilarity() {
    WS4JConfiguration.getInstance().setMFS(true);
    WS4JConfiguration.getInstance().setCache(true);
    LEMMATIZE = true;
  }

  @Override
  public double patentSim(Patent p1, Patent p2) {
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
  
//  @Override
//  public double patentSim(Patent p1, Patent p2) {
//    List<SAO> l1 = p1.getSaoTupleList();
//    List<SAO> l2 = p2.getSaoTupleList();
//    double total1 = 0, total2 = 0;
//
//    for (SAO t1 : l1) {
//      // find max pair for t1
//      double max = 0;
//      for (SAO t2 : l2) {
//        double tmp = saoSim(t1, t2);
//        if (tmp > max)
//          max = tmp;
//      }
//      total1 = total1 + max;
//    }
//
//    for (SAO t2 : l2) {
//      // find max pair for t2
//      double max = 0;
//      for (SAO t1 : l1) {
//        double tmp = saoSim(t1, t2);
//        if (tmp > max)
//          max = tmp;
//      }
//      total2 = total2 + max;
//    }
//
//    double sim = ((total1 / l1.size()) + (total2 / l2.size())) / 2;
//    return sim;
//  }

  @Override
  public double saoSim(SAO t1, SAO t2) {
    double s1 = pharseSim(t1.getString("subject"), t2.getString("subject"));
    double s2 = pharseSim(t1.getString("predicate"), t2.getString("predicate"));
    double s3 = pharseSim(t1.getString("object"), t2.getString("object"));
    double s4 = pharseSim(t1.getString("subject"), t2.getString("object"));
    double s5 = pharseSim(t1.getString("object"), t2.getString("subject"));
    return (s1 + s2 + s3) / 3;
    //return (s1 + s2 + s3 + s4 + s5) / 5;
  }

  @Override
  public double pharseSim(String pharse1, String pharse2) {
    String[] words1 = pharse1.split(" ");
    String[] words2 = pharse2.split(" ");
    double sum = 0;

    for (String word1 : words1) {
      for (String word2 : words2) {
        sum = sum + wordSim(word1, word2);
      }
    }

    return sum / (words1.length * words2.length);
  }

  @Override
  public double wordSim(String word1, String word2) {
    Pair<String, String> pair = new Pair<String, String>(word1, word2);
    if (cache.containsKey(pair))
      return cache.get(pair);
    
    if (LEMMATIZE) {
      word1 = stanford.getLemma(word1);
      word2 = stanford.getLemma(word2);
    }
    
    if (word1.equals(word2)) return 1;
    
    double value = WS4J.runWUP(word1, word2);
    if (value > 1) {
      value = 1;
    } else if (value < 0) {
      value = 0;
    }
    cache.put(pair, value);
    // System.out.println(value + " (" + word1 + "," + word2 + ")");
    return value;
  }

  public static class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
      this.left = left;
      this.right = right;
    }

    public L getLeft() {
      return left;
    }

    public R getRight() {
      return right;
    }

    @Override
    public int hashCode() {
      return left.hashCode() ^ right.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o == null)
        return false;
      if (!(o instanceof Pair))
        return false;
      Pair<?, ?> pairo = (Pair<?, ?>) o;
      return (this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight())) || (this.right.equals(pairo.getLeft()) && this.left.equals(pairo.getRight()));
    }

  }

}
