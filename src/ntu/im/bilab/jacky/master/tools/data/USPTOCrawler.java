package ntu.im.bilab.jacky.master.tools.data;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class USPTOCrawler {
	private static USPTOCrawler instance = null;
	private String baseURL = "http://patft1.uspto.gov";
	private String patentURL = "http://patft1.uspto.gov/netacgi/nph-Parser?patentnumber=";

	public static USPTOCrawler getInstance() {
		if (instance == null) {
			instance = new USPTOCrawler();
		}
		return instance;
	}

	public String crawlFullText(String id) throws IOException {
		String url = patentURL + id;
		Document doc = Jsoup.connect(url).timeout(3000).get();

		Elements meta = doc.select("html head meta");
		if (meta.attr("http-equiv").contains("REFRESH")) {
			doc = Jsoup
					.connect(baseURL + meta.attr("content").split("URL=")[1])
					.get();
		}
		return doc.body().text().trim();
	}

}
