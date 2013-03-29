package tools.sim;

import item.Patent;
import item.SAOTuple;

import java.io.IOException;
import java.util.List;

public class PatentSimilarity {
	private static PatentSimilarity instance = null;
	private String similarityType = "google1";
	private GoogleSimilarity gsim = GoogleSimilarity.getInstance();
	private WordNetSimilarity wsim = WordNetSimilarity.getInstance();

	public static PatentSimilarity getInstance() {
		if (instance == null) {
			instance = new PatentSimilarity();
		}
		return instance;
	}

	private double getWordSim(String w1, String w2, String type)
			throws IOException {
		if (similarityType.equals("google")) {
			return gsim.getGoogleDistance(w1, w2);
		} else {
			return wsim.getSim(w1, w2, type);
		}
	}

	private double getPairTupleSim(SAOTuple t1, SAOTuple t2) throws IOException {
		double s1 = getWordSim(t1.getSubject(), t2.getSubject(), "n");
		double s2 = getWordSim(t1.getPredicate(), t2.getPredicate(), "v");
		double s3 = getWordSim(t1.getObject(), t2.getObject(), "n");
		return (s1 + s2 + s3) / 3.0;
	}

	public double getPatentDissim(Patent p1, Patent p2) throws IOException {
		List<SAOTuple> l1 = p1.getSaoTupleList();
		List<SAOTuple> l2 = p2.getSaoTupleList();
		double total1 = 0, total2 = 0;

		for (SAOTuple t1 : l1) {
			// find max pair for t1
			double max = 0;
			for (SAOTuple t2 : l2) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total1 = total1 + max;
		}

		for (SAOTuple t2 : l2) {
			// find max pair for t2
			double max = 0;
			for (SAOTuple t1 : l1) {
				double tmp = getPairTupleSim(t1, t2);
				if (tmp > max)
					max = tmp;
			}
			total2 = total2 + max;
		}

		double sim = ((total1 / l1.size()) + (total2 / l2.size())) /2 ;

		return 1 - sim;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
