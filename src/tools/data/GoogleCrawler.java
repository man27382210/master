package tools.data;

import item.Patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.MakeInstrumentationUtil;

public class GoogleCrawler {
	private static GoogleCrawler instance = null;
	private String patentURL = "http://www.google.com/patents/";
	private String area = "US";

	// singleton
	public static GoogleCrawler getInstance() {
		if (instance == null) {
			instance = new GoogleCrawler();
		}
		return instance;
	}

	public Map<String, Object> crawl(String id) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		String url = patentURL + area + id;
		Document doc = Jsoup.connect(url).timeout(3000).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52").get();
		Element content = doc.getElementById("intl_patents_v");

		map.put("title", parseTitle(content));
		map.put("date", parsePublicationDate(content));
		map.put("inventors", parseInventors(content));
		map.put("applicant", parseApplicant(content));
		map.put("uspc", parseUSPC(content));
		map.put("ipc", parseIPC(content));
		map.put("abstract", parseAbstract(content));
		map.put("claims", parseClaims(content));
		map.put("description", parseDescription(content));
		map.put("backward_citations", parseBackwardCitations(content));
		map.put("forward_citations", parseForwardCitations(content));

		return map;
	}

	private String parseTitle(Element e) {
		String title = e.select("div[class=number-and-title] span[class=patent-title]").text();
		System.out.println("title : " + title);
		return title;
	}

	private String parsePublicationDate(Element e) {
		String date = e.select("table[class^=patent-bibdata] tr:contains(Publication date) td[class=single-patent-bibdata]").text();
		System.out.println("date : " + date);
		return date;
	}

	private List<String> parseInventors(Element e) {
		List<String> list = new ArrayList<String>();
		Elements inventors = e.select("table[class^=patent-bibdata] tr:contains(Inventors) div[class=patent-bibdata-value]");
		for (Element inventor : inventors) {
			list.add(inventor.text());
		}
		System.out.println("inventors : " + list);
		return list;
	}

	private String parseApplicant(Element e) {
		String applicant = e.select("table[class^=patent-bibdata] tr:contains(Applicant) div[class=patent-bibdata-value]").text();
		System.out.println("applicant : " + applicant);
		return applicant;
	}

	private List<String> parseUSPC(Element e) {
		List<String> list = new ArrayList<String>();
		Elements uspcs = e.select("table[class^=patent-bibdata] tr:contains(U.S. Classification) div[class=patent-bibdata-value]");
		for (Element uspc : uspcs) {
			list.add(uspc.text());
		}
		System.out.println("uspc : " + list);
		return list;
	}

	private List<String> parseIPC(Element e) {
		List<String> list = new ArrayList<String>();
		Elements ipcs = e.select("table[class^=patent-bibdata] tr:contains(International Classification) div[class=patent-bibdata-value]");
		for (Element ipc : ipcs) {
			list.add(ipc.text());
		}
		System.out.println("ipc : " +list);
		return list;
	}

	private String parseAbstract(Element e) {
		String abs = e.select("div[class=patent-section patent-abstract-section] div[class=patent-text]").text();
		System.out.println("abs :　" + abs);
		return abs;
	}

	private String parseClaims(Element e) {
		String clm = e.select("div[class=patent-section patent-claims-section] div[class=patent-text] div[class=claim]").text();
		System.out.println("clm :　" + clm);
		return clm;

	}

	private String parseDescription(Element e) {
		String des = e.select("div[class=patent-section patent-description-section] div[class=patent-text]").text();
		System.out.println("des :　" + des);
		return des;
	}

	private List<String> parseBackwardCitations(Element e) {
		List<String> list = new ArrayList<String>();
		Element table = e.select("div[class=patent-section patent-citations-section]:has(a#backward-citations) table[class=patent-data-table] tbody").first();
		if (table != null) {
			for (Element row : table.select("tr")) {
				Elements tds = row.select("td");
				list.add(tds.get(0).text());
				// System.out.println(tds.get(0).text());
			}
		}
		System.out.println("bc : " + list);
		return list;
	}

	private List<String> parseForwardCitations(Element e) {
		List<String> list = new ArrayList<String>();
		Element table = e.select("div[class=patent-section patent-citations-section]:has(a#forward-citations) table[class=patent-data-table] tbody").first();
		if (table != null) {
			for (Element row : table.select("tr")) {
				Elements tds = row.select("td");
				list.add(tds.get(0).text());
			}
		}
		System.out.println("fc : " + list);
		return list;
	}
	
	public void insert(String id) throws IOException {
		GoogleCrawler crawler = GoogleCrawler.getInstance();
		Map<String,Object> map = crawler.crawl(id);
		Patent p = new Patent();
		p.set("patent_id", "US" + id);
		p.set("title", map.get("title"));
		p.set("date", map.get("date"));
		p.set("inventors", map.get("inventors").toString());
		p.set("applicant", map.get("applicant"));
		p.set("uspc", map.get("uspc").toString());
		p.set("ipc", map.get("ipc").toString());
		p.set("abstract", map.get("abstract"));
		p.set("claims", map.get("claims"));
		p.set("description", map.get("description"));
		p.set("backward_citations", map.get("backward_citations").toString());
		p.set("forward_citations", map.get("forward_citations").toString());
		p.set("dataset", "dataset1");
		p.insert();
	}
}
