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
		  //list = list.subList(0, 2);
			TFIDFRanker ranker = TFIDFRanker.getInstance();
			ranker.load(list);
			
			SAOFilter filter = SAOFilter.getInstance();
			
			for (Patent p : list) {
				System.out.println(p.getSaoTupleList());
				filter.filteSAOTupleList(p);
				System.out.println(p.getSaoTupleList());
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