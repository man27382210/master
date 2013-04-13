package tools.sim;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import tools.data.GoogleCrawler;

public class GoogleSimilarity {

	public static GoogleSimilarity instance = null;
	private String baseUrl = "https://www.google.com/search?tbm=pts&q=";

	// singleton pattern
	public static GoogleSimilarity getInstance() {
		if (instance == null) {
			instance = new GoogleSimilarity();
		}
		return instance;
	}

	// get patent count in patent search
	public int crawlPageCount(String query) throws IOException, InterruptedException {
		delay();
		query = query.replaceAll("[\\s_]", "+");
		query = "\"" + query + "\"";
		String url = baseUrl + query;
		System.out.println(url);
		String agent1 = "Mozilla/5.0 (Windows NT 5.1; rv:5.0.1) Gecko/20100101 Firefox/5.0.1";
		String agent2 = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52";
		String agent3 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
		Document doc = Jsoup.connect(url).timeout(3000).userAgent(agent3).get();

		Element result = doc.getElementById("resultStats");
		if (result == null)
			return 0;
		String text = result.text();
		if (text.contains("About") && text.contains("result")) {
			text = text.substring(text.indexOf("About") + 6, text.indexOf("results") - 1).replaceAll(",", "");
		} else if (!text.contains("About") && text.contains("result")) {

			text = text.substring(0, text.indexOf("result") - 1).replaceAll(",", "");
		}

		int value = Integer.parseInt(text);
		System.out.println(value);
		return value;
	}

	//
	public double getGoogleDistance(String x, String y) throws IOException, InterruptedException {
		int M = 1380000000;

		if (x.equals(y))
			return 0;
		double LogOfXhits = Math.log(crawlPageCount(x));
		double LogOfYhits = Math.log(crawlPageCount(y));
		double LogOfXYhits = Math.log(crawlPageCount(x + "\"+\"" + y));
		double LogOfM = Math.log(M);

		double distance = (Math.max(LogOfXhits, LogOfYhits) - LogOfXYhits) / (LogOfM - Math.min(LogOfXhits, LogOfYhits));

		if (distance < 0) {
			distance = 0;
		} else if (distance > 1) {
			distance = 1;
		}

		System.out.println(distance);
		return distance;
	}

	public void delay() throws InterruptedException {
		long ms = 3000;
		Thread.sleep((long) (Math.random() * ms));
	}

	public static void main(String[] args) throws IOException {
		GoogleSimilarity sim = GoogleSimilarity.getInstance();
		try {
			for (int i = 1; i <= 1000; i++) {
				sim.crawlPageCount("test");
				System.out.println(i);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
