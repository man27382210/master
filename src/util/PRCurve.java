package util;

import item.PatentMap;
import item.Patent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;

import tools.data.DBManager;

public class PRCurve {

	// Assume the following:
	// • A database contains 80 records on a particular topic
	// • A search was conducted on that topic and 60 records were retrieved.
	// • Of the 60 records retrieved, 45 were relevant.
	//
	// The Precision and Recall could be calculated as follows
	//
	// Using the designations above:
	// • A = The number of relevant records retrieved,
	// • B = The number of relevant records not retrieved, and
	// • C = The number of irrelevant records retrieved.
	//
	// In this example A = 45, B = 35 (80-45) and C = 15 (60-45).
	//
	// Recall = (45 / (45 + 35)) * 100.0 => 45/80 * 100.0 = 56%
	// Precision = (45 / (45 + 15)) * 100.0 => 45/60 * 100.0 = 75%

	// In classification task,
	// Precision = TP / ( TP + FP ) or named "Positive Predictive Value"
	// Recall = TP / ( TP + FN ) or named "True Positive Rate", "Sensitivity"

	// True negative rate = TN / ( TN + FP )
	// Accuracy = ( TP + TN ) / TOTAL
	
	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();

		List<Patent> predictedList = Patent.findAll();
		draw(predictedList);
		mgr.close();
	}

	public static void draw(List<Patent> predictedList)
			throws FileNotFoundException, IOException {

		List<Patent> actualList = new ArrayList<Patent>();
		getActural(predictedList, actualList);

		int total = predictedList.size();
		int answer = actualList.size();

		double recall_bound = 0.0;
		List<Patent> list = new ArrayList<Patent>();
		System.out.println("precision , recall");
		for (int i = 0; i < total; i++) {
			list.add(predictedList.get(i));
			int tp = 0, fp = 0;
			for (Patent p : list) {
				if (actualList.contains(p)) {
					tp++;
				} else {
					fp++;
				}
			}
			int fn = answer - tp;

			double precision = (double) tp / (double) (tp + fp);
			double recall = (double) tp / (double) (tp + fn);

			if (recall >= recall_bound) {
				show(precision, recall);
				recall_bound += 0.05;
			}
		}
	}

	public static void show(double precision, double recall) {
		precision = roundTwoDecimals(precision);
		recall = roundTwoDecimals(recall);
		System.out.println(precision + " , " + recall);
	}

	public static void getActural(List<Patent> predictedList,
			List<Patent> actualList) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(
				"doc/dataset1-actural.txt"));
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
		DecimalFormat twoDForm = new DecimalFormat("0.00");
		return Double.valueOf(twoDForm.format(d));
	}
}
