package ntu.im.bilab.jacky.master.tools.nlp;

import java.util.List;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.item.SAOTuple;

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
