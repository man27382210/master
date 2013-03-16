package ntu.im.bilab.jacky.master.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ntu.im.bilab.jacky.master.Patent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleCrawler {

	private String baseURL = "http://www.google.com/patents/";
	private String area = "US";

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
		String url = baseURL + area + id;
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
	
	private void s(String s) {
		System.out.println(s);
	}
	
}
