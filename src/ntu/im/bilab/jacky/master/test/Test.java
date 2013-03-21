package ntu.im.bilab.jacky.master.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntu.im.bilab.jacky.master.Patent;
import ntu.im.bilab.jacky.master.patent.PatentMapGenerator;
import ntu.im.bilab.jacky.master.patent.SAOExtractor;

public class Test {

	public static void main(String[] args) throws IOException {
		List<Patent> list = new ArrayList<Patent>(); 
		
		Patent p = Patent.getPatent("8350616");
		SAOExtractor saoe = SAOExtractor.getInstance();
		p.setSaoTupleList(saoe.getSAO(p.getDescription()));
		p.show();
		list.add(p);
		
		p = Patent.getPatent("8350614");
		p.setSaoTupleList(saoe.getSAO(p.getDescription()));
		p.show();
		list.add(p);
		
		p = Patent.getPatent("8350611");
		p.setSaoTupleList(saoe.getSAO(p.getDescription()));
		p.show();
		list.add(p);
		
		PatentMapGenerator pmg = new PatentMapGenerator();
		pmg.getPatentMap(list);
		
	}

}
