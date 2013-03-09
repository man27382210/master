package ntu.im.bilab.jacky.master.test;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ntu.im.bilab.jacky.master.patent.JWSFetcher;
import ntu.im.bilab.jacky.master.patent.Patent;
import ntu.im.bilab.jacky.master.patent.SAOTuple;

import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;

public class JWSTest {
	
	public static void main(String[] args) {
		Patent p1,p2 = new Patent();
		
		SAOTuple t1 = new SAOTuple("","apple","love","apple");
		SAOTuple t2 = new SAOTuple("","apple","like","apple");
		SAOTuple t3 = new SAOTuple("","apple","hate","apple");
		SAOTuple t4 = new SAOTuple("","apple","draw","apple");
		SAOTuple t5 = new SAOTuple("","apple","watch","apple");
		
		List<SAOTuple> l1,l2 = new ArrayList();
		l1.add(t1);
		l1.add(t2);
		l1.add(t3);
		
		l2.add(t4);
		l2.add(t5);
		
		p1.setSAOList(l1);
		p2.setSAOList(l2);
		
		
		JWSFetcher jwsf = new JWSFetcher();
		jwsf = getMaxSimularity()
	}	
	
	
	
}
