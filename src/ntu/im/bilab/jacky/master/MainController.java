package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.tools.data.FileManager;
import ntu.im.bilab.jacky.master.tools.data.GoogleCrawler;
import ntu.im.bilab.jacky.master.tools.data.PatentFetcher;
import ntu.im.bilab.jacky.master.tools.data.USPTOCrawler;
import ntu.im.bilab.jacky.master.tools.nlp.SAOExtractor;
import ntu.im.bilab.jacky.master.tools.sim.PatentMapGenerator;

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
			
			List<Patent> patentList = (List<Patent>) mgr.readObjectFromFile("data/dataset1.txt");
			patentList = patentList.subList(0, 3);
			for (Patent p : patentList) {
				String id = p.getId();
				p.setSaoTupleList(extractor.getSAOTupleList(p.getFullText()));
				System.out.println("SAO-Extracting patent " + id + " ... done.");
				logger.info("SAO-Extracting patent " + id + " ... done.");
			}
				
			PatentMapGenerator pmg = new PatentMapGenerator();
			pmg.getPatentMap(patentList);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
