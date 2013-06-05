package util;

import item.Patent;
import item.SAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import depreciate.SimilarityMatrix;

import main.TFIDFFilter;


import tools.data.DBManager;
import tools.evaluation.PRCurve;
import tools.nlp.TFIDFRanker;
import weka.classifiers.functions.LinearRegression;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.DatabaseLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.boundaryvisualizer.KDDataGenerator;
import weka.gui.explorer.ClustererAssignmentsPlotInstances;
import weka.gui.explorer.ClustererPanel;
import weka.gui.visualize.ClassPanel;
import weka.gui.visualize.Plot2D;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class WekaDemo {

	public static void main(String[] args) throws Exception {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();

		DatabaseLoader loader = new DatabaseLoader();
		loader.setSource(mgr.getUrl(), mgr.getUser(), mgr.getPassword());
		loader.setQuery("select * from patent_map where dataset='dataset1'");
		Instances data = loader.getDataSet();
		// System.out.println(data.toString());

		SimpleKMeans km = new SimpleKMeans();
		km.setMaxIterations(1000);
		km.setPreserveInstancesOrder(true);
		km.setNumClusters(11);

		FilteredClusterer fc = new FilteredClusterer();
		Remove rm = new Remove();
		rm.setAttributeIndices("1,10");
		
		
		fc.setFilter(rm);
		fc.setClusterer(km);
		fc.buildClusterer(data);

		int[] assignment = km.getAssignments();
		// System.out.println(assignment[0]);

		Instances centroids = km.getClusterCentroids();
		System.out.println(centroids.toString());

		Map<Patent, Double> map = new HashMap<Patent, Double>();
		
		for (int i = 0; i < km.getNumClusters(); i++) {
			double cx = centroids.get(i).value(0);
			double cy = centroids.get(i).value(1);

			for (int j = 0; j < data.size(); j++) {
				if (i == assignment[j]) {
					double x = data.get(j).value(1);
					double y = data.get(j).value(2);
					double distance = Math.pow(x - cx, 2) + Math.pow(y - cy, 2);
					String id = data.get(j).stringValue(0);
					Patent p = Patent.findById(id);
					map.put(p, distance);
				}
			}
		}

		System.out.println(map);

		ValueComparator bvc = new ValueComparator(map);
		Map<Patent, Double> sorted_map = new TreeMap<Patent, Double>(bvc);

		System.out.println("unsorted map: " + map);
		sorted_map.putAll(map);
		System.out.println("results: " + sorted_map);

		List<Patent> predictedList = new ArrayList<Patent>(map.keySet());
		
		PRCurve.draw(predictedList);
		
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(fc);
		eval.evaluateClusterer(data);

		System.out.println(eval.clusterResultsToString());

		ClustererAssignmentsPlotInstances plotInstances = new ClustererAssignmentsPlotInstances();
		plotInstances.setClusterer(fc);
		plotInstances.setInstances(data);
		plotInstances.setClusterEvaluation(eval);
		plotInstances.setUp();
		PlotData2D dataset = plotInstances.getPlotData("plot name");

		System.out.println(plotInstances.getPlotInstances().toString());

		visualize(plotInstances, dataset);

		mgr.close();
	}

	private static void visualize(
			ClustererAssignmentsPlotInstances plotInstances, PlotData2D dataset)
			throws Exception {
		// generate visualization
		VisualizePanel visPanel = new VisualizePanel();
		visPanel.addPlot(dataset);

		// clean up
		plotInstances.cleanUp();

		final javax.swing.JFrame jf = new javax.swing.JFrame(
				"Weka Clusterer Visualize: " + "test");
		jf.setSize(500, 400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(visPanel, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setVisible(true);
	}
	
	public class ValueComparator implements Comparator<Patent> {
	  Map<Patent, Double> base;

	  public ValueComparator(Map<Patent, Double> base) {
	    this.base = base;
	  }

	  // Note: this comparator imposes orderings that are inconsistent with
	  // equals.
	  public int compare(Patent a, Patent b) {
	    if (base.get(a) >= base.get(b)) {
	      return -1;
	    } else {
	      return 1;
	    } // returning 0 would merge keys
	  }
	}
}
