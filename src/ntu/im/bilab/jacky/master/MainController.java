package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import ntu.im.bilab.jacky.master.db.DBSource;
import ntu.im.bilab.jacky.master.patent.Patent;
import ntu.im.bilab.jacky.master.patent.PatentFetcher;
import ntu.im.bilab.jacky.master.patent.SAOExtractor;
import ntu.im.bilab.jacky.master.tools.IssueYearFinder;

public class MainController {

	public static void main(String[] args) {
		try {
			// fetch all patent by query from USPTO and DB
			PatentFetcher pf = new PatentFetcher();
			List<Patent> patents = pf.getPatentList();
			System.out.println("Fetching content of patent done!");

			for (Patent p : patents) {
				String abstracts = p.getAbstracts();
				//System.out.println(p.getPatentId());
				//System.out.println(abstracts);
				p.setSAOList(SAOExtractor.getSAOTuple(abstracts));
				p.show();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
