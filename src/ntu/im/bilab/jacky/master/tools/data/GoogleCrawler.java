package ntu.im.bilab.jacky.master.tools.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ntu.im.bilab.jacky.master.item.Patent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

	public static void main(String[] args) {
		try {
			int i = 0;
			i = GoogleCrawler.crawlPageCount("uspto");
			System.out.println(i);
			i = GoogleCrawler.crawlPageCount("usptoasdasd");
			System.out.println(i);
			double d = GoogleCrawler.getGoogleDistance("serf", "search");
			System.out.println(d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static double getGoogleDistance(String x, String y) throws IOException {
		int M = 139000000;

		if (x.equals(y))
			return 0;
		double LogOfXhits = Math.log(crawlPageCount(x));
		double LogOfYhits = Math.log(crawlPageCount(y));
		double LogOfXYhits = Math.log(crawlPageCount(x + "+" + y));
		double LogOfM = Math.log(M);

		return (Math.max(LogOfXhits, LogOfYhits) - LogOfXYhits)
		    / (LogOfM - Math.min(LogOfXhits, LogOfYhits));
	}

	public static int crawlPageCount(String query) throws IOException {
		String url = "https://www.google.com/search?tbm=pts&q=" + query;
		Document doc = Jsoup
		    .connect(url)
		    .timeout(3000)
		    .userAgent(
		        "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52")
		    .get();

		Element result = doc.getElementById("resultStats");
		if (result == null)
			return 0;
		String text = result.text();
		text = text.substring(text.indexOf("About") + 6,
		    text.indexOf("results") - 1).replaceAll(",", "");
		return Integer.parseInt(text);
	}

	public Patent crawl(String id) throws IOException {
		Document doc = crawlPatent(id);
		Map<String, String> map = parsePatent(doc);

		Patent p = new Patent();
		p.setId(id);
		p.setAbstracts(map.get("Abstract"));
		p.setClaims(map.get("Claims"));
		p.setDescription(map.get("Description"));

		return p;
	}

	public void crawl(Patent p) throws IOException {
		String id = p.getId();
		Document doc = crawlPatent(id);
		Map<String, String> map = parsePatent(doc);

		p.setId(id);
		p.setAbstracts(map.get("Abstract"));
		p.setClaims(map.get("Claims"));
		p.setDescription(map.get("Description"));
	}

	private Document crawlPatent(String id) throws IOException {
		String url = patentURL + area + id;
		return Jsoup
		    .connect(url)
		    .timeout(3000)
		    .userAgent(
		        "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52")
		    .get();
	}

	private Map<String, String> parsePatent(Document doc) {
		Map<String, String> map = new HashMap<String, String>();

		Element content = doc.getElementById("intl_patents_v");
		map.putAll(parseAbstract(content));
		map.putAll(parseClaims(content));
		map.putAll(parseDescription(content));

		return map;
	}

	private Map<String, String> parseAbstract(Element e) {
		Map<String, String> map = new HashMap<String, String>();
		String key = "Abstract";
		String value = e
		    .select(
		        "div[class=patent-section patent-abstract-section] div[class=patent-text]")
		    .text();
		map.put(key, value);
		return map;
	}

	private Map<String, String> parseClaims(Element e) {
		Map<String, String> map = new HashMap<String, String>();
		String key = "Claims";
		String value = e
		    .select(
		        "div[class=patent-section patent-claims-section] div[class=patent-text]")
		    .text();
		map.put(key, value);
		return map;
	}

	private Map<String, String> parseDescription(Element e) {
		Map<String, String> map = new HashMap<String, String>();
		String key = "Description";
		String value = e
		    .select(
		        "div[class=patent-section patent-description-section] div[class=patent-text]")
		    .text();
		map.put(key, value);
		return map;
	}

}
