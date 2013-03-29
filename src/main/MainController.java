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
import tools.nlp.Stemmer;
import tools.sim.WordNetSimilarity;


public class MainController {
	
	private static Logger logger = Logger.getLogger("Main");
	
	public static void main(String[] args) {
		try {
			FileManager mgr = new FileManager();
			List<Patent> list = (List<Patent>) mgr.readObjectFromFile("data/dataset1-alltuple.txt");
			TFIDFRanker ranker = TFIDFRanker.getInstance();
			ranker.load(list);
			
			SAOFilter filter = SAOFilter.getInstance();
			for (Patent p : list) {
				System.out.println(p.getSaoTupleList());
				filter.filterSAOTupleList(p);
				System.out.println(p.getSaoTupleList());
			}
			
			Stemmer stemmer = Stemmer.getInstance();
			stemmer.stem(list);
			
			PatentMapGenerator g = new PatentMapGenerator();
			g.getPatentMap(list);
			
			WordNetSimilarity w = WordNetSimilarity.getInstance();
			System.out.println("zero = "+w.zero); 
			System.out.println("non-zero = "+w.nzero); 
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}