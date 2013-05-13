package tools.evaluation;

import item.PatentMap;
import item.Patent;
import item.Patents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;

import tools.data.DBManager;
import util.MakeInstrumentationUtil;

public class PRCurve {

  // In classification task,
  // Precision = TP / ( TP + FP ) or named "Positive Predictive Value"
  // Recall = TP / ( TP + FN ) or named "True Positive Rate", "Sensitivity"

  // True negative rate = TN / ( TN + FP )
  // Accuracy = ( TP + TN ) / TOTAL

  public static void evaluate(List<String> predictedList, List<String> actualList) throws FileNotFoundException, IOException {

    int total = predictedList.size();
    int answer = actualList.size();
    double max_accuracy = 0;
    double max_f1 = 0;

    double recall_bound = 0.0;
    List<String> list = new ArrayList<String>();
    System.out.println("recall , precision");
    for (int i = 0; i < total; i++) {
      list.add(predictedList.get(i));
      int tp = 0, fp = 0;
      for (String id : list) {
        if (actualList.contains(id)) {
          tp++;
        } else {
          fp++;
        }
      }
      int fn = answer - tp;
      int tn = total - fn - tp - fp;

      double precision = (double) tp / (double) (tp + fp);
      double recall = (double) tp / (double) (tp + fn);
      double accuracy = (double) (tp + tn) / (double) total;
      if (accuracy > max_accuracy)
        max_accuracy = accuracy;
      double f1 = 2 * precision * recall / (precision + recall);
      if (f1 > max_f1)
        max_f1 = f1;

      show(precision, recall);
      
//      if (recall >= recall_bound) {
//        show(precision, recall_bound);
//        recall_bound += 0.05;
//      }
    }

    System.out.println("Accuracy : " + max_accuracy + " F1 : " + max_f1);
  }

  public static void show(double precision, double recall) {
    recall = roundTwoDecimals(recall);
    precision = roundTwoDecimals(precision);
    System.out.println(recall + " , " + precision);
  }

  public static void getActural(List<Patent> predictedList, List<Patent> actualList, String fileName) throws IOException {
    List<String> list = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    String line = null;
    while ((line = br.readLine()) != null) {
      list.add(line);
    }
    br.close();

    for (Patent p : predictedList) {
      if (list.contains(p.getId()))
        actualList.add(p);
    }
  }

  public static double roundTwoDecimals(double d) {
    DecimalFormat twoDForm = new DecimalFormat("0.000");
    return Double.valueOf(twoDForm.format(d));
  }

  public static void evaluate(Patents dataset) throws FileNotFoundException, IOException {
    evaluate(dataset.loadRank(), dataset.getNovelIdList());
  }
}
