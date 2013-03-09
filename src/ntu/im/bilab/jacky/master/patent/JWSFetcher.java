package ntu.im.bilab.jacky.master.patent;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;

public class JWSFetcher {
	JWS jws;
	
	public JWSFetcher() {
		String dir = "wordnet";
		jws = new JWS(dir, "3.0");
	}
	
	public double getMaxSimularity(String word1 , String word2 , String type) {
		JiangAndConrath jcn = jws.getJiangAndConrath();
		return jcn.max(word1,word2,type);
	}
	
	public double getPatentDissimularity(Patent p1, Patent p2){
		
		for (p1.getSAOList())
	}
}
