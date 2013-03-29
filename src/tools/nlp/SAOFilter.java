package tools.nlp;

import item.Patent;
import item.SAOTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.TFIDFRanker;

public class SAOFilter {
	private static SAOFilter instance = null;
	private Patent patent = null;
	private TFIDFRanker ranker = TFIDFRanker.getInstance();

	public static SAOFilter getInstance() {
		if (instance == null) {
			instance = new SAOFilter();
		}
		return instance;
	}

	public void filteSAOTupleList(Patent patent) {
		this.patent = patent;
		List<SAOTuple> list = patent.getSaoTupleList();
		SAOTupleComparator comparator = new SAOTupleComparator();
		Collections.sort(list, comparator);
		list = list.subList(0, 5);
		patent.setSaoTupleList(list);
	}

	public class SAOTupleComparator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			SAOTuple t1 = (SAOTuple) arg0;
			SAOTuple t2 = (SAOTuple) arg1;
			String id = patent.getId();

			double v1 = ranker.getTFIDF(id, t1.getSubject())
			    + ranker.getTFIDF(id, t1.getPredicate())
			    + ranker.getTFIDF(id, t1.getObject());

			double v2 = ranker.getTFIDF(id, t2.getSubject())
			    + ranker.getTFIDF(id, t2.getPredicate())
			    + ranker.getTFIDF(id, t2.getObject());
			
			Double d1 = new Double(v1);
			Double d2 = new Double(v2);
			
			return d1.compareTo(d2);
		}
	}

}
