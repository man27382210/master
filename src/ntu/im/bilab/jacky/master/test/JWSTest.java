package ntu.im.bilab.jacky.master.test;
import java.util.TreeMap;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;

public class JWSTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "wordnet";
		JWS ws = new JWS(dir, "3.0");
		JiangAndConrath jcn = ws.getJiangAndConrath();
		TreeMap<String, Double> scores1 = jcn.jcn("apple", "apple tree", "n");
		for(String s : scores1.keySet())
			System.out.println(s + "\t" + scores1.get(s));
		//jcn.max(arg0, arg1, arg2)
	}

}
