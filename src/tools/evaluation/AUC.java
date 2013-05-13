package tools.evaluation;

import item.Patent;
import item.Patents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AUC {
  
  public static double evaluate(List<String> rankInstances, List<String> pInstances) throws IOException {
    
    int total = rankInstances.size();
    int p = pInstances.size();
    int n = total - p;

    int sum = 0;
    for (int i = 0; i < rankInstances.size(); i++) {
      int rank = total - i;
      if (pInstances.contains(rankInstances.get(i))) {
        sum = sum + rank;
      }
    }

    double auc = (sum - (p * (p + 1)) / (double) 2) / (p * n);
    System.out.println("auc : " + auc);
    return auc;
  }

  public static void evaluate(Patents dataset) throws IOException {
    evaluate(dataset.loadRank(), dataset.getNovelIdList());
  }
  
}
