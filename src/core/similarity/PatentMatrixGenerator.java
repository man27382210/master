package core.similarity;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import core.dbmodel.Patent;

import mdsj.MDSJ;


public class PatentMatrixGenerator {

  private static List<Similarity> simList;

  public static void setSimilarity(List<Similarity> simList) {
    PatentMatrixGenerator.simList = simList;
  }

  public static void setSimilarity(Similarity sim) {
    simList = new ArrayList<Similarity>();
    simList.add(sim);
  }
  
  public static void generate(List<Patent> list) throws IOException, InterruptedException, ParseException {
    int size = list.size();
    double[][] input = new double[size][size];

    for (int i = 0; i < size; i++) {
      Map<Patent, Double> map = new HashMap<Patent, Double>();
      Patent p1 = list.get(i);
      for (int j = 0; j < size; j++) {
        Patent p2 = list.get(j);
        if (i == j) {
          input[i][j] = 0;
        } else if (i > j) {
          input[i][j] = input[j][i];
        } else {
          long d0 = System.currentTimeMillis();
          double sum = 0.0;
          for(int index = 0 ; index < simList.size() ; index++) {
            double sim = simList.get(index).patentSim(p1, p2);
            sum += sim;
          }
          double sim = sum / simList.size();
          input[i][j] = 1 - sim;
          
          long d1 = System.currentTimeMillis();
          //System.out.println("Fetching dissim between " + p1.getId() + " and " + p2.getId() + " : " + input[i][j] + " time : " + (d1 - d0) + "ms");
        }
        map.put(p2, input[i][j]);
      }
      p1.setDissimMap(map);
    }

    double[][] output = MDSJ.classicalScaling(input); // apply MDS
    for (int i = 0; i < list.size(); i++) { // output all coordinates
      // System.out.println(output[0][i] + " " + output[1][i]);
    }

    //show(output, list);
  }

  public static void show(double[][] data, List<Patent> list) {
    XYSeriesCollection dataset = new XYSeriesCollection();
    for (int i = 0; i < list.size(); i++) {
      XYSeries serie = new XYSeries("US" + list.get(i).getId());
      serie.add(data[0][i], data[1][i]);
      dataset.addSeries(serie);
    }

    JFreeChart chart = ChartFactory.createScatterPlot(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainZeroBaselineVisible(true);
    plot.setRangeZeroBaselineVisible(true);
    plot.setDomainZeroBaselinePaint(Color.BLUE);
    plot.setRangeZeroBaselinePaint(Color.BLUE);

    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMaximumFractionDigits(2); // etc.
    XYItemLabelGenerator generator = new StandardXYItemLabelGenerator("{0}");
    XYItemRenderer renderer = plot.getRenderer();
    renderer.setBaseItemLabelGenerator(generator);
    renderer.setBaseItemLabelsVisible(true);

    ChartFrame frame = new ChartFrame("Test", chart);
    frame.pack();
    frame.setVisible(true);

  }
}
