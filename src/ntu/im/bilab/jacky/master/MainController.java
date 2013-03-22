package ntu.im.bilab.jacky.master;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ntu.im.bilab.jacky.master.item.Patent;
import ntu.im.bilab.jacky.master.tools.data.DBSource;
import ntu.im.bilab.jacky.master.tools.data.GoogleCrawler;
import ntu.im.bilab.jacky.master.tools.data.IssueYearFinder;
import ntu.im.bilab.jacky.master.tools.nlp.SAOExtractor;

public class MainController {

	public static void s(String s) {
		System.out.println(s);
	}

	public static void main(String[] args) {
		try {

			// fetch all relative patent by query from USPTO
			// PatentFetcher pf = new PatentFetcher();
			// List<Patent> patentList = pf.getPatentList();
			// s("Relative patent query loading.");

			List<Patent> patentList = new ArrayList<Patent>();
			Patent test = new Patent();
			test.setId("8350614");
			patentList.add(test);

			// fetch content from google and get sao
			GoogleCrawler gc = new GoogleCrawler();
			SAOExtractor saoe = new SAOExtractor();
			for (Patent p : patentList) {
				gc.crawl(p);
				s("Crawling patent : " + p.getId());
				p.setSaoTupleList(saoe.getSAO(p.getDescription()));
				s("SAO-Extracting patent : " + p.getId());
				s(p.toString());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
