package tools.nlp;

import item.Patent;
import item.SaoTuple;

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

	public void filterSAOTupleList(List<Patent> list) {
		for (Patent p : list)
			filterSAOTupleList(p);
	}

	public void filterSAOTupleList(Patent patent) {
		this.patent = patent;
		List<SaoTuple> list = patent.getSaoTupleList();
		SAOTupleComparator comparator = new SAOTupleComparator();
		Collections.sort(list, comparator);
		list = list.subList(list.size() - 6, list.size() - 1);
		patent.setSaoTupleList(list);
	}

	public class SAOTupleComparator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			SaoTuple t1 = (SaoTuple) arg0;
			SaoTuple t2 = (SaoTuple) arg1;
			String id = (String) patent.get("patent_id");

			double v1 = ranker.getTFIDF(id, t1.getSubject())
					+ ranker.getTFIDF(id, t1.getPredicate())
					+ ranker.getTFIDF(id, t1.getObject());

			double v2 = ranker.getTFIDF(id, t2.getSubject())
					+ ranker.getTFIDF(id, t2.getPredicate())
					+ ranker.getTFIDF(id, t2.getObject());

			Double d1 = new Double(v1);
			Double d2 = new Double(v2);

			// System.out.println(d1 + t1.toString());
			// System.out.println(d2 + t2.toString());

			return d2.compareTo(d1);
		}
	}

}
