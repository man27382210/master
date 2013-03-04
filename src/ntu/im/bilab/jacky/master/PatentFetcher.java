package ntu.im.bilab.jacky.master;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PatentFetcher {

	// Given a result of query from uspto
	Document query_result;

	// Collected patents
	List<Document> patents = new ArrayList<Document>();

	// Count of relative patents from query
	int count_of_relative_patents;

	// id list of relative patents
	List<String> patent_id = new ArrayList<String>();
	
	// run fetch process
	public void execute(){
	
		String query = fetchQueryString();
		System.out.println("Query : " + query);
		fetchQueryResult(query,1);
		countOfRelativePatents();
		System.out.println("Relative Patents : " + count_of_relative_patents);
		fetchRelativePatentId();
		
		int page = (int) (count_of_relative_patents / 50) + 1;
		if(page>1){
			for(int i = 2; i<page+1 ; i++){
				fetchQueryResult(query,page);
				fetchRelativePatentId();
			}
		}
		
	}
	
	// get all queried patents' id
	public List<String> getPatentIdList(){
		return patent_id;
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
			patent_id.add(id);
		}

	}

	// get patent query from uspto advance search
	private void fetchQueryResult(String query, int page) {

		// replace all char for URL conversion
		query = query.replaceAll(" ", "%20");
		String url = "http://patft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=0&p=" + page + "&f=S&l=50&Query="
				+ query + "&d=PTXT";

		try {
			// set timeout = 0 for timeout problem (0 -> infinite)
			query_result = Jsoup.connect(url).timeout(0).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// load query string from file
	private String fetchQueryString() {
		String query = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"doc/query.txt"));
			query = br.readLine();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

	// get info of focal patent from uspto
	private Document fetchPatentDocumentById(String patent_id) {
		patent_id = patent_id.replaceAll(",", "");
		String url = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PALL&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.htm&r=1&f=G&l=50&s1="
				+ patent_id + ".PN.&OS=PN/" + patent_id + "&RS=PN/" + patent_id;
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(0).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
}