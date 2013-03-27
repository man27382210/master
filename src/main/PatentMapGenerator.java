package main;

import item.Patent;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mdsj.MDSJ;

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

import tools.sim.PatentSimilarity;
import tools.sim.WordNetSimilarity;

public class PatentMapGenerator {

	private List<PatentMap> map = new ArrayList<PatentMap>();

	public class PatentMap {
	}

	public List<PatentMap> getPatentMap(List<Patent> list) throws IOException {
		int size = list.size();
		PatentSimilarity sim = PatentSimilarity.getInstance();
		double[][] input = new double[size][size];

		int x = 0, y = 0;
		for (Patent p1 : list) {
			for (Patent p2 : list) {
				if (x == y) {
					input[x][y] = 0;
					y++;
					continue;
				} else if (x > y) {
					input[x][y] = input[y][x];
					y++;
					continue;
				} else {
					input[x][y] = sim.getPatentDissim(p1, p2);
					System.out.println("Fetching sim between " + p1.getId() + " and "
					    + p2.getId() + " : " + input[x][y]);

					y++;
				}
			}
			x++;
			y = 0;
		}

		double[][] output = MDSJ.classicalScaling(input); // apply MDS
		for (int i = 0; i < list.size(); i++) { // output all coordinates
			System.out.println(output[0][i] + " " + output[1][i]);
		}

		show(output, list);
		return map;
	}

	public void show(double[][] data, List<Patent> list) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (int i = 0; i < list.size(); i++) {
			XYSeries serie = new XYSeries("US" + list.get(i).getId());
			serie.add(data[0][i], data[1][i]);
			dataset.addSeries(serie);
		}

		JFreeChart chart = ChartFactory.createScatterPlot(null, null, null,
		    dataset, PlotOrientation.VERTICAL, true, true, false);
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
