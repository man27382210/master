package tools.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IssueYearFinder {
	// mapping range of patent_number with issue year and type
	private Map<String, String> utility = new LinkedHashMap<String, String>();
	private Map<String, String> design = new LinkedHashMap<String, String>();
	private Map<String, String> plant = new LinkedHashMap<String, String>();
	private Map<String, String> reissue = new LinkedHashMap<String, String>();
	private Map<String, String> sir = new LinkedHashMap<String, String>();

	public IssueYearFinder() {
		String data = "";
		try {
			// read the mapping file
			BufferedReader br = new BufferedReader(new FileReader("doc/issuyear.txt"));
			while ((data = br.readLine()) != null) {
				// preprocessing
				data = data.replaceAll("[a-zA-Z]+", "");
				String[] line = data.split("\\s+");
				String year = line[0];
				// putting into each map by type
				utility.put(year, line[1]);
				design.put(year, line[2]);
				plant.put(year, line[3]);
				reissue.put(year, line[4]);
				if (line.length > 5)
					sir.put(year, line[5]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// get the issue year of focal patent
	public String getIssueYear(String patent_id) {
		Map<String, String> map = null;
		// extract the type by string patent_id
		char c = patent_id.charAt(0);
		if ('1' <= c && c <= '9') {
			map = utility;
		} else if (c == 'D') {
			map = design;
		} else if (c == 'P') {
			map = plant;
		} else if (c == 'R') {
			map = reissue;
		} else if (c == 'H') {
			map = sir;
		} else {
			map = null;
		}

		// remove , and english letter
		patent_id = patent_id.replaceAll(",", "");
		patent_id = patent_id.replaceAll("[a-zA-Z]+", "");
		String year = "0000";
		
		// clarify the issue year
		for (Entry<String, String> entry : map.entrySet()) {
			if (patent_id.compareTo(entry.getValue()) >= 0) {
				year = entry.getKey();
			} else {
				break;
			}
		}
		
		return year;
	}

}
