package depreciate;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class USPTOSimularity {
	private static USPTOSimularity instance = null;

	public static USPTOSimularity getInstance() {
		if (instance == null) {
			instance = new USPTOSimularity();
		}
		return instance;
	}

	// remove elements based on tag list
	private Document removeElements(Document doc, String[] list) {
		for (int i = 0; i < list.length; i++)
			doc.getElementsByTag(list[i]).remove();
		return doc;
	}

	public void crawlPageCount(String query) throws IOException {
		query = query.replaceAll(" ", "%20");
		String url = "http://patft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=0&p=1" + "&f=S&l=50&Query=" + query + "&d=PTXT";
		Document doc = Jsoup.connect(url).timeout(0).get();
		String[] tag_list = { "head", "center", "form", "table", "p", "b", "i" };
		doc = removeElements(doc, tag_list);
		String s = doc.text();
		int count = Integer.parseInt(s = s.substring(2, s.indexOf("patents") - 1));
		System.out.println(count);
	}

	public static void main(String[] args) {

		try {
			USPTOSimularity sim = USPTOSimularity.getInstance();
			long t0 = System.currentTimeMillis();
			sim.crawlPageCount("\"transmission\"");
			long t1 = System.currentTimeMillis();
			System.out.println("Done in " + (t1 - t0) + " msec.");
			
			t0 = System.currentTimeMillis();
			sim.crawlPageCount("ABST/\"transmission\" or ACLM/\"transmission\"");
			t1 = System.currentTimeMillis();
			System.out.println("Done in " + (t1 - t0) + " msec.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
