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
import ntu.im.bilab.jacky.master.tools.IssueYearFinder;

public class MainController {

	public static void main(String[] args) {
		try {
			IssueYearFinder iyf = new IssueYearFinder();
			PatentFetcher pf = new PatentFetcher();
			pf.execute();
			List<Patent> patents = pf.getPatentList();
			DBSource db = new DBSource();
			Connection conn = db.getConnection();
			Statement stmt = conn.createStatement();
			
			patents = patents.subList(0, 1);
			
			
			for (Patent p : patents) {
				String patent_id = p.getPatentId();
				String year = iyf.getIssueYear(patent_id);
				p.setYear(year);
				String sql = "select abstract from uspto_" + year
						+ " where patent_id = '" + patent_id + "'";
				// System.out.println(sql);
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String abstracts = rs.getString("Abstract");
					abstracts = abstracts.substring(abstracts.indexOf("Abstract")+9);
					//System.out.println(abstracts);
					p.setAbstracts(abstracts);
					// System.out.println(claims);
				}
			}

			db.closeConnection(conn);
			
			System.out.println("Fetching content of patent done!");
			
			for (Patent p : patents) {
				String abstracts = p.getAbstracts();
				System.out.println(p.getPatentId());
				System.out.println(abstracts);
				p.setSAOList(SAOExtractor.getSAOTuple(abstracts)) ;
				System.out.println(p.toString());
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
