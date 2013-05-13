package tools.sim;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

public class PhraseWNSimilarity extends WNSimilarity {
  @Override
  public double pharseSim(String pharse1, String pharse2) {
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

//  @Override
//  public double pharseSim(String pharse1, String pharse2) {
//    String[] words1 = pharse1.split(" ");
//    String[] words2 = pharse2.split(" ");
//    double sum = 0;
//
//    for (String word1 : words1) {
//      double max_sim = 0;
//      for (String word2 : words2) {
//        double sim = wordSim(word1, word2);
//        if (sim > max_sim)
//          max_sim = sim;
//      }
//      sum += max_sim;
//    }
//
//    // return sum / words1.length;
//
//    double sum2 = 0;
//
//    for (String word2 : words2) {
//      double max_sim2 = 0;
//      for (String word1 : words1) {
//        double sim2 = wordSim(word1, word2);
//        if (sim2 > max_sim2)
//          max_sim2 = sim2;
//      }
//      sum2 += max_sim2;
//    }
//
//    // if (sum / words1.length > sum2/ words2.length) {
//    // return sum2 / words2.length;
//    // } else {
//    // return sum / words1.length;
//    // }
//
//    return (sum / words1.length + sum2 / words2.length) / 2;
//  }

}
