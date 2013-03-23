package ntu.im.bilab.jacky.master.tools.sim;

import java.awt.Color;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mdsj.MDSJ;
import ntu.im.bilab.jacky.master.item.Patent;

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

public class PatentMapGenerator {

	private List<PatentMap> map = new ArrayList<PatentMap>();

	public class PatentMap {
	}

	public List<PatentMap> getPatentMap(List<Patent> list) {
		int size = list.size();
		JWSFetcher fetcher = JWSFetcher.getInstance();
		double[][] input = new double[size][size];

		int x = 0, y = 0;
		for (Patent p1 : list) {
			for (Patent p2 : list) {
				input[x][y] = fetcher.getPatentDissim(p1, p2);
				if (x == y)
					input[x][y] = 0;
				y++;
			}
			x++;
			y = 0;
		}

		System.out.println(input[0][0]);
		System.out.println(input[0][1]);
		System.out.println(input[1][0]);
		System.out.println(input[1][1]);

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
		XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(
		    "{0}");
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setBaseItemLabelGenerator(generator);
		renderer.setBaseItemLabelsVisible(true);

		ChartFrame frame = new ChartFrame("Test", chart);
		frame.pack();
		frame.setVisible(true);

	}

}
