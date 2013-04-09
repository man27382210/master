package main;

import item.MakeInstrumentationUtil;
import item.Patent;
import item.SaoTuple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import tools.data.DBManager;
import tools.data.GoogleCrawler;
import tools.data.PatentFetcher;
import tools.data.USPTOCrawler;
import tools.nlp.SAOExtractor;
import tools.nlp.SAOFilter;
import tools.nlp.StanfordLemmatizer;
import tools.nlp.Stemmer;
import tools.sim.WordNetSimilarity;

public class MainController {

	private static Logger logger = Logger.getLogger("Main");

	public static void other() throws FileNotFoundException, IOException {

		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		List<Patent> patents = Patent.where("dataset = 'dataset1'");
		for (Patent p : patents) {
			String id = (String) p.get("id");
			p.setId(id);
			List<SaoTuple> tuples = SaoTuple.where("patent_id = '" + p.get("id") + "'");
			for (SaoTuple t : tuples) {
				t.setSubject((String) t.get("subject"));
				t.setPredicate((String) t.get("predicate"));
				t.setObject((String) t.get("object"));
			}
			p.setSaoTupleList(tuples);
		}

		TFIDFRanker ranker = TFIDFRanker.getInstance();
		ranker.load(patents);

		SAOFilter filter = SAOFilter.getInstance();
		for (Patent p : patents) {
			System.out.println(p.getSaoTupleList());
			filter.filterSAOTupleList(p);
			System.out.println(p.getSaoTupleList());
		}

		Stemmer stemmer = Stemmer.getInstance();
		stemmer.stem(patents);

		PatentMapGenerator g = new PatentMapGenerator();
		g.getPatentMap(patents);

		WordNetSimilarity w = WordNetSimilarity.getInstance();
		System.out.println("zero = " + w.zero);
		System.out.println("non-zero = " + w.nzero);

	}

	public static void main(String[] args) {
		
		try {
		  // savePatentUSPTO();
			// savePatentGoogle();
			 saveSAOTuple();
			// lemmatizeSAOTuple();
			// other();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void lemmatizeSAOTuple() throws FileNotFoundException, IOException {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		List<SaoTuple> tuples = SaoTuple.findAll();

		StanfordLemmatizer lemmatizer = StanfordLemmatizer.getInstance();

		for (SaoTuple t : tuples) {
			t.set("subject", lemmatizer.getLemma((String) t.get("subject")));
			t.set("predicate", lemmatizer.getLemma((String) t.get("predicate")));
			t.set("object", lemmatizer.getLemma((String) t.get("object")));
			t.saveIt();
		}
	}

	public static void saveSAOTuple() throws FileNotFoundException, IOException {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		List<Patent> patents = Patent.where("dataset = 'dataset1'");
		SAOExtractor extractor = SAOExtractor.getInstance();
		for (Patent p : patents) {
			String id = p.getString("id");
			System.out.println("save sao into db : " + id);
			String content = p.getString("abstract") + p.getString("claims") + p.getString("description");
			List<SaoTuple> tuples = extractor.getSAOTupleList(content);
			int count = 0;

			for (SaoTuple t : tuples) {
				System.out.println("sao number : " + ++count);
				t.set("patent_id", id);
				t.insert();
			}
		}
		mgr.close();
	}

	public static void savePatentUSPTO() throws IOException {
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

	public static void savePatentGoogle() throws IOException {

		MakeInstrumentationUtil.make();
		PatentFetcher fetcher = new PatentFetcher();
		List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");

		DBManager mgr = DBManager.getInstance();
		mgr.open();
		GoogleCrawler crawler = GoogleCrawler.getInstance();
		for (String id : idList) {
			// exist patent then skip
			if (Patent.findById("US" + id) != null)
				continue;
			System.out.println("crawl patent : " + id);
			crawler.insert(id);
		}

		mgr.close();

	}
}