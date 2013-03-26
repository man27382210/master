package tools.nlp;

import item.Patent;
import item.SAOTuple;

import java.util.List;


public class SAOFilter {
	private static SAOFilter instance = null;
	
	public static SAOFilter getInstance() {
		if (instance == null) {
			instance = new SAOFilter();
		}
		return instance;
	}
	
	public void filteSAOTupleList(Patent p) {
		List<SAOTuple> list = p.getSaoTupleList();
		list = list.subList(0, 5);
		p.setSaoTupleList(list);
	}
}
