package util;

import item.Map;
import item.Patent;
import item.SaoTuple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.PatentMapGenerator;
import main.TFIDFRanker;

import tools.data.DBManager;
import tools.nlp.SAOFilter;
import tools.sim.SimilarityMatrix;
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
		System.out.println(data.toString());
	  
		SimpleKMeans km = new SimpleKMeans();
		km.setMaxIterations(1000);
		km.setPreserveInstancesOrder(true);
		km.setNumClusters(5);
		
		FilteredClusterer fc = new FilteredClusterer();
		
		Remove rm = new Remove();
		rm.setAttributeIndices("1,4");
		fc.setFilter(rm);
		fc.setClusterer(km);
		
		fc.buildClusterer(data);
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
		// generate visualization
		VisualizePanel visPanel = new VisualizePanel();
		visPanel.addPlot(dataset);
		
		// clean up
		plotInstances.cleanUp();

		final javax.swing.JFrame jf = new javax.swing.JFrame("Weka Clusterer Visualize: " + "test");
		jf.setSize(500, 400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(visPanel, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setVisible(true);
		
		mgr.close();
	}

	public static void show(List<Patent> list, double[][] data) throws Exception {
		// number of instance
		int size = list.size();
		List<Instance> instances = new ArrayList<Instance>();
		for (int i = 0; i < size; i++)
			instances.add(new DenseInstance(3));

		Attribute id = new Attribute("id", (ArrayList<String>) null, 0);
		Attribute x = new Attribute("x-value", 1);
		Attribute y = new Attribute("y-value", 2);
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(id);
		attributes.add(x);
		attributes.add(y);

		for (int i = 0; i < size; i++) {
			Instance obj = instances.get(i);
			obj.setValue(id, list.get(i).getString("patent_id"));
			obj.setValue(x, data[0][i]);
			obj.setValue(y, data[1][i]);
		}

		Instances newDataset = new Instances("Dataset", attributes, size);
		for (Instance inst : instances)
			newDataset.add(inst);

		System.out.println(newDataset.toString());
		
		SimpleKMeans km = new SimpleKMeans();
		km.setMaxIterations(1000);
		km.setPreserveInstancesOrder(true);
		km.setNumClusters(5);
		
		FilteredClusterer fc = new FilteredClusterer();
		
		Remove rm = new Remove();
		rm.setAttributeIndices("1");
		fc.setFilter(rm);
		fc.setClusterer(km);
		
		fc.buildClusterer(newDataset);
		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(fc);
		eval.evaluateClusterer(newDataset);
	
		System.out.println(eval.clusterResultsToString());
		

		ClustererAssignmentsPlotInstances plotInstances = new ClustererAssignmentsPlotInstances();
		plotInstances.setClusterer(fc);
		plotInstances.setInstances(newDataset);
		plotInstances.setClusterEvaluation(eval);
		plotInstances.setUp();
		PlotData2D dataset = plotInstances.getPlotData("plot name");
		int[] shape = dataset.getShapeSize();
		for (int i = 0; i < shape.length; i++) {
			shape[i]++;
		}
		dataset.setShapeSize(shape);

		// generate visualization
		VisualizePanel visPanel = new VisualizePanel();
		visPanel.addPlot(dataset);
		
		// clean up
		plotInstances.cleanUp();

		final javax.swing.JFrame jf = new javax.swing.JFrame("Weka Clusterer Visualize: " + "test");
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
}
