package ntu.im.bilab.jacky.master.patent;

import java.util.List;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.Lin;

public class JWSFetcher {
	JWS jws;
	
	public JWSFetcher() {
		String dir = "wordnet";
		jws = new JWS(dir, "3.0");
	}
	
	public double getMaxSimularity(String word1 , String word2 , String type) {
		Lin lin = jws.getLin();
		return lin.max(word1,word2,type);
	}
	
	public double getPatentDissimularity(Patent p1, Patent p2){
		List<SAOTuple> l1 = p1.getSAOList();
		List<SAOTuple> l2 = p2.getSAOList();
		double sum = 0.0;
		for (SAOTuple t1 : l1) {
			for (SAOTuple t2 : l2) {
				double simularity = 0.0;
				double s1 = getMaxSimularity(t1.getSubject(),t2.getSubject(),"n");
				double s2 = getMaxSimularity(t1.getPredicate(),t2.getPredicate(),"v");
				double s3 = getMaxSimularity(t1.getObject(),t2.getObject(),"n");
				simularity = (s1+s2+s3)/3.0;
				sum = sum + simularity;
			}
		}
		return 1-(sum/(l1.size()*l2.size()));	
	}
}
