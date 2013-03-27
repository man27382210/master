package main;

import item.Patent;
import item.SAOTuple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tools.data.FileManager;
import tools.data.GoogleCrawler;
import tools.data.PatentFetcher;
import tools.data.USPTOCrawler;
import tools.nlp.SAOExtractor;
import tools.nlp.SAOFilter;


public class MainController {
	
	private static Logger logger = Logger.getLogger("Main");
	
	public static void main(String[] args) {
		try {
			FileManager mgr = new FileManager();
			List<Patent> list = (List<Patent>) mgr.readObjectFromFile("data/dataset1-alltuple.txt");
			//list = list.subList(0, 15);
			for (Patent p : list) {
				List<SAOTuple> tl = p.getSaoTupleList();
				if(tl.size() > 10) tl = tl.subList(0, 1);
				p.setSaoTupleList(tl);
			}
			
			PatentMapGenerator g = new PatentMapGenerator();
			g.getPatentMap(list);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}