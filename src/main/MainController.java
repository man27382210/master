package main;

import item.Patent;

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
import tools.sim.PatentMapGenerator;


public class MainController {
	
	private static Logger logger = Logger.getLogger("Main");
	
	public static void main(String[] args) {
		try {
//			PatentFetcher fetcher = new PatentFetcher();
//			List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");
//			System.out.println("Collecting patent_id dataset from file ... done.");
//			logger.info("Collecting patent_id dataset from file ... done.");
//			List<Patent> patentList = new ArrayList<Patent>();
//			USPTOCrawler crawler = USPTOCrawler.getInstance();
//			SAOExtractor extractor = SAOExtractor.getInstance();
			
			FileManager mgr = new FileManager();
			SAOExtractor extractor = SAOExtractor.getInstance();
			SAOFilter filter = SAOFilter.getInstance();
			
			
			List<Patent> patentList = (List<Patent>) mgr.readObjectFromFile("data/dataset1.txt");
			//patentList = patentList.subList(0, 3);
			int i = 1;
			for (Patent p : patentList) {
				String id = p.getId();
				p.setSaoTupleList(extractor.getSAOTupleList(p.getFullText()));
				//filter.filteSAOTupleList(p);
				System.out.println(i++ + ". SAO-Extracting patent " + id + ", " + p.getSaoTupleList().size() + " tuples fetched");
				logger.info("SAO-Extracting patent " + id + " ... done.");
			}
			
			mgr.writeObjectToFile("data/dataset1-alltuple.txt", patentList);
				
			//PatentMapGenerator pmg = new PatentMapGenerator();
			//pmg.getPatentMap(patentList);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}