package tools.sim;

import item.Patent;
import item.SaoTuple;

import java.io.IOException;
import java.util.List;

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

	private double getPairTupleSim(SaoTuple t1, SaoTuple t2) throws IOException, InterruptedException {
		double s1 = getWordSim(t1.getString("subject"), t2.getString("subject"), "n");
		double s2 = getWordSim(t1.getString("predicate"), t2.getString("predicate"), "v");
		double s3 = getWordSim(t1.getString("object"), t2.getString("object"), "n");
		return (s1 + s2 + s3) / 3.0;
	}

	public double getPatentDissim(Patent p1, Patent p2) throws IOException, InterruptedException {
		List<SaoTuple> l1 = p1.getSaoTupleList();
		List<SaoTuple> l2 = p2.getSaoTupleList();
		double total1 = 0, total2 = 0;

		for (SaoTuple t1 : l1) {
			// find max pair for t1
			double max = 0;
			for (SaoTuple t2 : l2) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total1 = total1 + max;
		}

		for (SaoTuple t2 : l2) {
			// find max pair for t2
			double max = 0;
			for (SaoTuple t1 : l1) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total2 = total2 + max;
		}

		//System.out.println(total1);
		
		double sim = ((total1 / l1.size()) + (total2 / l2.size())) /2 ;

		return 1 - sim;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
