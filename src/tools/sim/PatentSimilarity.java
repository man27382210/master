package tools.sim;

import item.Patent;
import item.SAO;

import java.io.IOException;
import java.util.List;

import mdsj.MDSJ;

public class PatentSimilarity {
	private static PatentSimilarity instance = null;
	private String similarityType = "wordnet";
	private GoogleSimilarity gsim = GoogleSimilarity.getInstance();
	private WordNetSimilarity wsim = WordNetSimilarity.getInstance();

	public static PatentSimilarity getInstance() {
		if (instance == null) {
			instance = new PatentSimilarity();
		}
		return instance;
	}

	private double getWordSim(String w1, String w2, String type)
			throws IOException, InterruptedException {
		if (similarityType.equals("google")) {
			return gsim.getGoogleDistance(w1, w2);
		} else {
			double value = wsim.getSim(w1, w2, type);
			return value;
		}
	}

	private double getPairTupleSim(SAO t1, SAO t2) throws IOException, InterruptedException {
		double s1 = getWordSim(t1.getString("subject"), t2.getString("subject"), "n");
		double s2 = getWordSim(t1.getString("predicate"), t2.getString("predicate"), "v");
		double s3 = getWordSim(t1.getString("object"), t2.getString("object"), "n");
		return (s1 + s2 + s3) / 3.0;
	}

	public double getPatentSim(Patent p1, Patent p2) throws IOException, InterruptedException {
		List<SAO> l1 = p1.getSaoTupleList();
		List<SAO> l2 = p2.getSaoTupleList();
		double total1 = 0, total2 = 0;

		for (SAO t1 : l1) {
			// find max pair for t1
			double max = 0;
			for (SAO t2 : l2) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total1 = total1 + max;
		}

		for (SAO t2 : l2) {
			// find max pair for t2
			double max = 0;
			for (SAO t1 : l1) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total2 = total2 + max;
		}

		//System.out.println(total1);
		
		double sim = ((total1 / l1.size()) + (total2 / l2.size())) /2 ;

		return sim;
	}

	public void getPatentMap(List<Patent> list) throws IOException, InterruptedException {
//		int size = list.size();
//		PatentSimilarity sim = PatentSimilarity.getInstance();
//		double[][] input = new double[size][size];
//
//		int x = 0, y = 0;
//		for (Patent p1 : list) {
//			for (Patent p2 : list) {
//				if (x == y) {
//					input[x][y] = 0;
//					y++;
//					continue;
//				} else if (x > y) {
//					input[x][y] = input[y][x];
//					y++;
//					continue;
//				} else {
//					input[x][y] = sim.getPatentDissim(p1, p2);
//					System.out.println("Fetching dissim between " + p1.getId() + " and " + p2.getId() + " : " + input[x][y]);
//					y++;
//				}
//			}
//			x++;
//			y = 0;
//		}
//
//		double[][] output = MDSJ.classicalScaling(input); // apply MDS
//		for (int i = 0; i < list.size(); i++) { // output all coordinates
//			System.out.println(output[0][i] + " " + output[1][i]);
//		}
	}

}
