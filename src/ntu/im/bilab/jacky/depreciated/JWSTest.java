package ntu.im.bilab.jacky.depreciated;

import java.util.ArrayList;
import java.util.List;
import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.item.SAOTuple;
import ntu.im.bilab.jacky.master.tools.sim.JWSFetcher;

public class JWSTest {

	public static void main(String[] args) {
		Patent p1 = new Patent();
		Patent p2 = new Patent();
		
		SAOTuple t1 = new SAOTuple("", "apple", "love", "apple");
		SAOTuple t2 = new SAOTuple("", "orange", "like", "pie");
		SAOTuple t3 = new SAOTuple("", "apple", "hate", "apple");
		SAOTuple t4 = new SAOTuple("", "device","draw", "apple");
		SAOTuple t5 = new SAOTuple("", "apple", "watch", "bird");

		List<SAOTuple> l1 = new ArrayList<SAOTuple>();
		List<SAOTuple> l2 = new ArrayList<SAOTuple>();
		l1.add(t1);
		l1.add(t2);
		l1.add(t3);

		l2.add(t4);
		l2.add(t5);

		p1.setSaoTupleList(l1);
		p2.setSaoTupleList(l2);

		JWSFetcher jwsf = new JWSFetcher();
		double d = jwsf.old_getPatentDissimularity(p1, p2);
		System.out.println(d);
	}
}
