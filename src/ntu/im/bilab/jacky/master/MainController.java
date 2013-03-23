package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.tools.data.GoogleCrawler;
import ntu.im.bilab.jacky.master.tools.data.PatentFetcher;
import ntu.im.bilab.jacky.master.tools.data.USPTOCrawler;
import ntu.im.bilab.jacky.master.tools.nlp.SAOExtractor;
import ntu.im.bilab.jacky.master.tools.sim.PatentMapGenerator;

public class MainController {
	
	private static Logger logger = Logger.getLogger("Main");
	
	public static void main(String[] args) {
		try {
			// fetch dataset1's id
			PatentFetcher fetcher = new PatentFetcher();
			List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");
			System.out.println("Collecting patent_id dataset from file ... done.");
			logger.info("Collecting patent_id dataset from file ... done.");
			
			// fetch google data by id
			List<Patent> patentList = new ArrayList<Patent>();
			USPTOCrawler crawler = USPTOCrawler.getInstance();
			SAOExtractor extractor = SAOExtractor.getInstance();
			for (String id : idList) {
				Patent p = new Patent();
				p.setId(id);
				p.setFullText(crawler.crawlFullText(id));
				
				System.out.println("Crawling patent " + id + " ... done.");
				logger.info("Crawling patent " + id + " ... done.");

				patentList.add(p);
				
				// fetch sao tuples of data
				p.setSaoTupleList(extractor.getSAOTupleList(p.getFullText()));
				
				System.out.println("SAO-Extracting patent " + id + " ... done.");
				logger.info("SAO-Extracting patent " + id + " ... done.");
				
				if (patentList.size() > 3) break;
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
