package tools.sim;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class PatentLens {
	private final static int result_total_doc = 81727381;
	
	public static Document getDocument(String url) throws IOException {
		Document doc = Jsoup.connect(url).timeout(0).get();
		return doc;
	}
	
	public static int count(String word) throws IOException {
		word = word.replaceAll(" ", "+");
		String url = "http://www.lens.org/lens/search?ft=true&q=" + word ;
		Document doc = getDocument(url);
		String text = doc.getElementById("previousSearch").text();
		int value = Integer.parseInt(text);
		return value;
	}
	
	public static double distance(String word1 , String word2) throws IOException {
		int M = result_total_doc;
		String x = word1;
		String y = word2;
		if (x.equals(y)) return 0;
		double LogOfXhits = Math.log(count(x));
		double LogOfYhits = Math.log(count(y));
		double LogOfXYhits = Math.log(count(x + " %26%26 " + y));
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
	
	public static void main(String[] args) throws IOException {
		int i = count("zinc device");
		System.out.println(i);
		
		double d = distance("epicyclic gear","satellite gear");
		System.out.println(d);
	}

	
}
