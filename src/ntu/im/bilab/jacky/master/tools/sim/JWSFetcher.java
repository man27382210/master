package ntu.im.bilab.jacky.master.tools.sim;

import java.util.List;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.item.SAOTuple;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;

public class JWSFetcher {
	private JWS jws;
	private static JWSFetcher instance;
	
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

	public double getMaxSimularity(String word1, String word2, String type) {
		if (word1.equals(word2)) return 1;
		Lin lin = jws.getLin();
		return lin.max(word1, word2, type);
	}

	public double getPatentDissimularity(Patent p1, Patent p2) {
		List<SAOTuple> l1 = p1.getSaoTupleList();
		List<SAOTuple> l2 = p2.getSaoTupleList();
		double sum = 0.0;
		for (SAOTuple t1 : l1) {
			for (SAOTuple t2 : l2) {
				double simularity = 0.0;
				double s1 = getMaxSimularity(t1.getSubject(), t2.getSubject(), "n");
				double s2 = getMaxSimularity(t1.getPredicate(), t2.getPredicate(), "v");
				double s3 = getMaxSimularity(t1.getObject(), t2.getObject(), "n");
				simularity = (s1 + s2 + s3) / 3.0;
				sum = sum + simularity;
			}
		}
		return 1 - (sum / (l1.size() * l2.size()));
	}
	
	public static void main(String[] args){
		JWSFetcher f = JWSFetcher.getInstance();
		double s1 = f.getMaxSimularity("lovey", "lovey", "n");
		System.out.println(s1);
	}
}
