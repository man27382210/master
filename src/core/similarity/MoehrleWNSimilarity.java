package core.similarity;


import java.util.List;

import core.dbmodel.Patent;
import core.dbmodel.SAO;

public class MoehrleWNSimilarity extends WNSimilarity {

  @Override
  public double patentSim(Patent p1, Patent p2) {
    List<SAO> l1 = p1.getSaoTupleList();
    List<SAO> l2 = p2.getSaoTupleList();

    int Cji = 0;
    for (SAO s2 : l2) {
      double max_sim = 0;
      for (SAO s1 : l1) {
        double sim = saoSim(s1,s2);
        if (sim > max_sim) max_sim = sim; 
      }
      if (max_sim > 0.5) Cji++;
    }
    int Cj = l2.size();
        
    double sim = (double) Cji / (double) Cj;
    System.out.println(sim);
    return sim;
  }

  public static void main(String[] args) {
    MoehrleWNSimilarity w = new MoehrleWNSimilarity();
    double d = w.pharseSim("a", "a");
    System.out.println(d);

  }
}
