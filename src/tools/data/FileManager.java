package tools.data;

import item.Patent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

	// write patent to file (text)
	public void writePatentToFile(String fileDir, Patent p) throws IOException {
		String fileName = fileDir + "US" + p.getId() + ".txt";
		File file = new File(fileName);
		if (!file.exists())
			file.createNewFile();
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		br.write(p.getFullText());
		br.close();
	}

	// read patent from file (text)
	public String readPatentFromFile(String fileDir, Patent p) throws IOException {
		String fileName = fileDir + "US" + p.getId() + ".txt";
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		br.close();
		return line;
	}

	// write patent list to file (text)
	public void writePatentListToFile(List<Patent> list, String fileDir)
	    throws IOException {
		for (Patent p : list)
			writePatentToFile(fileDir, p);
	}

	public void readPatentListFromFile(List<Patent> list, String fileDir)
	    throws IOException {
		for (Patent p : list)
			p.setFullText(readPatentFromFile(fileDir, p));
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
			mgr.writePatentListToFile(patentList, "data/dataset1/");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
