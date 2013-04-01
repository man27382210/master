package main;

import item.MakeInstrumentationUtil;
import item.Patent;
import item.SaoTuple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tools.data.DBManager;
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

	public static void other() {
		try {
			FileManager mgr = new FileManager();
			List<Patent> list = (List<Patent>) mgr
			    .readObjectFromFile("data/dataset1-alltuple.txt");
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
			System.out.println("zero = " + w.zero);
			System.out.println("non-zero = " + w.nzero);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			// savePatent()
			saveSAOTuple();
			// other();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveSAOTuple() throws FileNotFoundException, IOException {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		List<Patent> patents = Patent.where("remark = 'dataset1'");
		SAOExtractor extractor = SAOExtractor.getInstance();
		for (Patent p : patents) {
			String id = (String) p.get("id");
			System.out.println("save sao into db : " + id);
			String fullText = (String) p.get("full_text");
			List<SaoTuple> tuples = extractor.getSAOTupleList(fullText);
			int count = 0;
			for (SaoTuple t : tuples) {
				System.out.println("sao number : " + ++count);
				t.set("patent_id", id);
				t.set("subject", t.getSubject());
				t.set("predicate", t.getPredicate());
				t.set("object", t.getObject());
				t.insert();
			}
		}
		mgr.close();
	}

	public static void savePatent() throws IOException {
		MakeInstrumentationUtil.make();

		PatentFetcher fetcher = new PatentFetcher();
		List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");

		USPTOCrawler crawler = USPTOCrawler.getInstance();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		for (String id : idList) {
			System.out.println("save patent into db : " + id);
			String fullText = crawler.crawlFullText(id);

			if (Patent.findById(id) != null)
				continue;

			Patent p = new Patent();
			p.set("id", id);
			p.set("full_text", fullText);
			p.set("remark", "dataset1");
			p.insert();
		}
		mgr.close();
	}
}