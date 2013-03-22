package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.tools.data.GoogleCrawler;
import ntu.im.bilab.jacky.master.tools.data.PatentFetcher;
import ntu.im.bilab.jacky.master.tools.data.USPTOCrawler;
import ntu.im.bilab.jacky.master.tools.nlp.SAOExtractor;
import ntu.im.bilab.jacky.master.tools.sim.PatentMapGenerator;

public class MainController {

	public static void main(String[] args) {
		try {
			// fetch dataset1's id
			PatentFetcher fetcher = new PatentFetcher();
			List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");
			
			// fetch google data by id
			List<Patent> patentList = new ArrayList<Patent>();
			USPTOCrawler crawler = USPTOCrawler.getInstance();
			SAOExtractor extractor = SAOExtractor.getInstance();
			for (String id : idList) {
				System.out.println("crawl id : " + id);
				Patent p = new Patent();
				p.setId(id);
				p.setFullText(crawler.crawlFullText(id));
				System.out.println("crawl complete.");
				patentList.add(p);
				
				// fetch sao tuples of data
				p.setSaoTupleList(extractor.getSAOTupleList(p.getFullText()));
	
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

	public static void s(String s) {
		System.out.println(s);
	}

}
