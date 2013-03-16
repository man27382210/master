package ntu.im.bilab.jacky.master.patent;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ntu.im.bilab.jacky.master.Patent;
import ntu.im.bilab.jacky.master.db.DBSource;
import ntu.im.bilab.jacky.master.tools.IssueYearFinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PatentFetcher {

	// Given a result of query from uspto
	Document query_result;

	// Collected patents
	// List<Document> patents = new ArrayList<Document>();

	// Count of relative patents from query
	int count_of_relative_patents;

	// Patent List
	List<Patent> patents = new ArrayList<Patent>();

	// fetch information from database
	private void fetchRelativePatentByDB() throws FileNotFoundException,
	    IOException, ClassNotFoundException, SQLException {
		IssueYearFinder iyf = new IssueYearFinder();
		DBSource db = new DBSource();
		Connection conn = db.getConnection();
		Statement stmt = conn.createStatement();
		patents = patents.subList(0, 1);

		for (Patent p : patents) {
			String id = p.getId();
			String year = iyf.getIssueYear(id);
			p.setYear(year);
			String sql = "select abstract from uspto_" + year
			    + " where patent_id = '" + id + "'";
			// System.out.println(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String abstracts = rs.getString("Abstract");
				abstracts = abstracts.substring(abstracts.indexOf("Abstract") + 9);
				// System.out.println(abstracts);
				p.setAbstracts(abstracts);
				// System.out.println(claims);
			}
		}

		db.closeConnection(conn);
	}

	// fetch relative patent by quering USPTO
	private void fetchRelativePatentByUSPTO() throws IOException {
		String query = fetchQueryString();
		System.out.println("Query : " + query);
		fetchQueryResultByUSPTO(query, 1);
		countOfRelativePatents();
		System.out.println("Relative Patents : " + count_of_relative_patents);
		fetchRelativePatentId();

		int page = (int) (count_of_relative_patents / 50) + 1;
		if (page > 1) {
			for (int i = 2; i < page + 1; i++) {
				fetchQueryResultByUSPTO(query, page);
				fetchRelativePatentId();
			}
		}
	}

	// fetch and get all patent
	public List<Patent> getPatentList() throws FileNotFoundException,
	    IOException, ClassNotFoundException, SQLException {
		fetchRelativePatentByUSPTO();
		//fetchRelativePatentByDB();
		return patents;
	}

	// get relative patents' id
	private void fetchRelativePatentId() {

		Element table = query_result.getElementsByTag("table").get(1);
		Elements tr = table.getElementsByTag("tr");
		tr.remove(0);
		Iterator<Element> i = tr.iterator();
		while (i.hasNext()) {
			Element e = i.next();
			String id = e.getElementsByTag("td").get(1).text();
			id = id.replaceAll(",", "");
			Patent p = new Patent();
			p.setId(id);
			patents.add(p);
		}

	}

	// get patent query from uspto advance search
	private void fetchQueryResultByUSPTO(String query, int page)
	    throws IOException {
		// replace all char for URL conversion
		query = query.replaceAll(" ", "%20");
		
		String url = "http://patft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=0&p="
		    + page + "&f=S&l=50&Query=" + query + "&d=PTXT";
		query_result = Jsoup.connect(url).timeout(0).get();

	}

	// load query string from file
	private String fetchQueryString() throws IOException {
		String query = "";
		BufferedReader br = new BufferedReader(new FileReader("doc/query.txt"));
		query = br.readLine();
		br.close();
		return query;
	}

	// remove elements based on tag list
	private Document removeElements(Document doc, String[] list) {
		for (int i = 0; i < list.length; i++)
			doc.getElementsByTag(list[i]).remove();
		return doc;
	}

	// count relative patents
	private void countOfRelativePatents() {
		Document doc = query_result.clone();
		String[] tag_list = { "head", "center", "form", "table", "p", "b", "i" };
		doc = removeElements(doc, tag_list);
		String s = doc.text();
		count_of_relative_patents = Integer.parseInt(s = s.substring(2,
		    s.indexOf("patents") - 1));
	}

}