package tools.sim;

import item.Patent;
import item.SaoTuple;
import java.util.List;

import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;
import edu.sussex.nlp.jws.WuAndPalmer;

public class WordNetSimilarity {
	private static WordNetSimilarity instance = null;
	public int zero = 0;
	public int nzero = 0;

	public static WordNetSimilarity getInstance() {
		if (instance == null) {
			instance = new WordNetSimilarity();
		}
		return instance;
	}

	public WordNetSimilarity() {
		WS4JConfiguration.getInstance().setMFS(true);
	}

	public double getSim(String word1, String word2, String type) {
		double value = WS4J.runWUP(word1, word2);
		if(value > 1) value = 1;
		//System.out.println(value + " (" + word1 + "," + word2 + ")");
		return value;
	}

	public static void main(String[] args) {
		WordNetSimilarity w = WordNetSimilarity.getInstance();
		w.getSim("transmit", "lock", "n");

		WS4JConfiguration.getInstance().setMFS(true);
		WS4J.runWUP("transmit", "lock");
	}

}
