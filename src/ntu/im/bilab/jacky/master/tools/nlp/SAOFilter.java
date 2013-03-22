package ntu.im.bilab.jacky.master.tools.nlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SAOFilter {
	private static SAOFilter instance = null;
	private List<String> stopWordList;
	
	public static SAOFilter getInstance() throws IOException{
		if (instance == null ){
			instance = new SAOFilter();
			instance.loadStopWord();
		}
		return instance;
	}
	
	public boolean isStopWord(String word) {
		return stopWordList.contains(word);
	}

	private void loadStopWord() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("doc/stopword.txt"));
		String line = null;
		while ((line = br.readLine()) != null) {
			stopWordList.add(line);
		}
		br.close();
	}
	
	
//	@SuppressWarnings("unused")
//  private void show() throws IOException {
//		BufferedReader br = new BufferedReader(new FileReader("doc/stopword.txt"));
//		String line = null;
//		
//		List<String> l1 = new ArrayList<String>();
//		List<String> l2 = new ArrayList<String>();
//		List<String> l3 = new ArrayList<String>();
//		
//		while ((line = br.readLine()) != null) {
//			String[] words = line.split(" ");
//			l1.add(words[0]);
//			l2.add(words[1]);
//			l3.add(words[2]);
//		}
//		
//		for (String str : l1) {
//			System.out.println(str.trim());
//		}
//		
//		for (String str : l2) {
//			System.out.println(str.trim());
//		}
//		
//		for (String str : l3) {
//			System.out.println(str.trim());
//		}
//	}

	public static void main(String[] args) throws IOException {
		SAOFilter filter = new SAOFilter();
		
	}

}
