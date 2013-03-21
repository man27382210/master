package ntu.im.bilab.jacky.master.test;

import java.awt.Color;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class First {

	public static void main(String[] args) {

		DefaultXYDataset dataset = new DefaultXYDataset();
		double[][] series = { { -0.5, 0.4 }, { 1.3, -1.1 } };
		dataset.addSeries("series1", series);
		
		
		JFreeChart chart = ChartFactory.createScatterPlot(null, null, null, dataset,
		    PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot =  (XYPlot) chart.getPlot();
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);
		plot.setDomainZeroBaselinePaint(Color.BLUE);
		plot.setRangeZeroBaselinePaint(Color.BLUE);
		
		//chart.setBorderVisible(true);
		// create and display a frame...
		ChartFrame frame = new ChartFrame("Test", chart);
		frame.pack();
		frame.setVisible(true);
	}

}
