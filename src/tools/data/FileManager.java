package tools.data;

import item.Patent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public class FileManager {

	// write object to file (in json format)  
	public void writeObjectToFile(String fileName, Object obj) throws IOException {
		OutputStream out = new FileOutputStream(fileName);
		JsonWriter jw = new JsonWriter(out);
		jw.write(obj);
		jw.close();
	}

	// read object from file (in json format)
	public Object readObjectFromFile(String fileName) throws IOException {
		InputStream in = new FileInputStream(fileName);
		JsonReader jr = new JsonReader(in);
		Object obj = jr.readObject();
		jr.close();
		return obj;
	}

	public static void main(String args[]) {
		try {
			PatentFetcher fetcher = new PatentFetcher();
			List<String> idList = fetcher.fetchPatentByFile("doc/dataset1.txt");

			USPTOCrawler crawler = USPTOCrawler.getInstance();
			List<Patent> patentList = new ArrayList<Patent>();
			for (String id : idList) {
				System.out.println("Crawl Patent " + id);
				Patent p = new Patent();
				p.setId(id);
				p.setFullText(crawler.crawlFullText(id));
				patentList.add(p);
			}

			FileManager mgr = new FileManager();
			mgr.writeObjectToFile("data/dataset1.txt", patentList);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
