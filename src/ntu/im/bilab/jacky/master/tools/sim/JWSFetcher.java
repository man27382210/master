package ntu.im.bilab.jacky.master.tools.sim;

import java.util.List;

import org.apache.log4j.Logger;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.item.SAOTuple;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.Lin;

public class JWSFetcher {
	private JWS jws;
	private static JWSFetcher instance = null;
	private static Logger logger = Logger.getLogger("Test");

	public static JWSFetcher getInstance() {
		if (instance == null) {
			instance = new JWSFetcher();
		}
		return instance;
	}

	public JWSFetcher() {
		String dir = "wordnet";
		jws = new JWS(dir, "3.0");
	}

	public double getSim(String word1, String word2, String type) {
		if (word1.equals(word2))
			return 1;
		Lin lin = jws.getLin();
		double sim = lin.lin(word1, 1, word2, 1, type);
		logger.debug(sim+" (" + word1 + "," + word2 + "," + type + ")");

		return sim;
		// return lin.max(word1, word2, type); too slow
	}

	public double getPairTupleSim(SAOTuple t1, SAOTuple t2) {
		double s1 = getSim(t1.getSubject(), t2.getSubject(), "n");
		double s2 = getSim(t1.getPredicate(), t2.getPredicate(), "v");
		double s3 = getSim(t1.getObject(), t2.getObject(), "n");
		// if ((s1 + s2 + s3) / 3.0 > 0.5) return 1;
		return (s1 + s2 + s3) / 3.0;
	}

	public double getPatentDissim(Patent p1, Patent p2) {
		List<SAOTuple> l1 = p1.getSaoTupleList();
		List<SAOTuple> l2 = p2.getSaoTupleList();

		double total = 0;
		for (SAOTuple t1 : l1) {
			for (SAOTuple t2 : l2) {
				total = total + getPairTupleSim(t1, t2);
			}
		}

		return total * 2 / (l1.size() + l2.size());
	}

	public double old_getPatentDissimularity(Patent p1, Patent p2) {
		List<SAOTuple> l1 = p1.getSaoTupleList();
		List<SAOTuple> l2 = p2.getSaoTupleList();

		double sum = 0.0;
		for (SAOTuple t1 : l1) {
			for (SAOTuple t2 : l2) {
				double simularity = 0.0;
				double s1 = getSim(t1.getSubject(), t2.getSubject(), "n");
				double s2 = getSim(t1.getPredicate(), t2.getPredicate(), "v");
				double s3 = getSim(t1.getObject(), t2.getObject(), "n");
				simularity = (s1 + s2 + s3) / 3.0;
				sum = sum + simularity;
			}
		}
		return 1 - (sum / (l1.size() * l2.size()));
	}

	public static void main(String[] args) {
		JWSFetcher f = JWSFetcher.getInstance();
		logger.info("asd");
		for (int i = 0; i < 10; i++) {
			double s1 = f.getSim("cat", "dog", "n");
		}
		logger.info("asd2");
	}
}
