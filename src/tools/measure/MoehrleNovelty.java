package tools.measure;

import item.Patent;
import item.SAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tools.evaluation.AUC;
import tools.evaluation.PRCurve;

public class MoehrleNovelty {

	public static List<String> getRanking(List<Patent> patentList) throws FileNotFoundException, IOException {
		List<String> expect = new ArrayList<String>();
		for (Patent p : patentList) {
			 double d = getNovelty(p);
			 System.out.println(p.getId() + ":" + d);
		}
		
		Collections.sort(patentList, new NoveltyComparator());
		for(Patent p : patentList) {
			expect.add((String) p.getId());
		}
		
		return expect;
	}

	public static double getNovelty(Patent p1) {
		Map<Patent, Double> map = p1.getDissimMap();
		double maxSim = 0;
		Iterator<Entry<Patent, Double>> itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Entry e = itr.next();
			Patent p2 = (Patent) e.getKey();
			// only want patent p2 before p1
			if (p1.getString("patent_id").compareTo(p2.getString("patent_id")) > 0) {
				double dissim = (Double) e.getValue();
				double sim = 1 - dissim;
				if (sim > maxSim)
					maxSim = sim;
			}
		}
		return 1 - maxSim;
	}
	
	public static class NoveltyComparator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			Patent p1 = (Patent) arg0;
			Patent p2 = (Patent) arg1;

			double d1 = getNovelty(p1);
			double d2 = getNovelty(p2);
			
			// from max to min
			if (d1 > d2) return -1;
			if (d1 < d2) return 1;
      return 0;
		}
	}
}
