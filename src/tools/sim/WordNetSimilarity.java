package tools.sim;

import item.Patent;
import item.SaoTuple;
import java.util.List;
import org.apache.log4j.Logger;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;

public class WordNetSimilarity {
	private JWS jws;
	private static WordNetSimilarity instance = null;
	private static Logger logger = Logger.getLogger("Test");
	public int zero = 0;
	public int nzero = 0;

	public static WordNetSimilarity getInstance() {
		if (instance == null) {
			instance = new WordNetSimilarity();
		}
		return instance;
	}

	public WordNetSimilarity() {
		String dir = "wordnet";
		jws = new JWS(dir, "3.0");
	}

	public double getSim(String word1, String word2, String type) {
		if (word1.equals(word2))
			return 1;
		JiangAndConrath jcn = jws.getJiangAndConrath();
		Lin lin = jws.getLin();
		double sim = jcn.max(word1, word2, type);

		logger.debug(sim + " (" + word1 + "," + word2 + "," + type + ")");
		//System.out.println(sim + " (" + word1 + "," + word2 + "," + type + ")");
		if (sim == 0) {
			zero++;
		} else {
			nzero++;
		}
		
		if(sim < 0) {
			return 0;
		} else if (sim > 1){
			return 1;
		} else {
			return sim;
		}
	}

}
