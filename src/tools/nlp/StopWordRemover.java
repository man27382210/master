package tools.nlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StopWordRemover {
	private static StopWordRemover instance = null;
	private List<String> stopWordList = new ArrayList<String>();
	
	public static StopWordRemover getInstance() throws IOException{
		if (instance == null ){
			instance = new StopWordRemover();
			instance.loadStopWord();
		}
		return instance;
	}
	
	private boolean isStopWord(String word) {
		return stopWordList.contains(word);
	}

	private boolean isNumber(String word) {
		return word.matches(".*\\d.*");
	}
	
	public boolean matchFilter(String word) {
		return (isStopWord(word) || isNumber(word));
	}
	
	private void loadStopWord() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("doc/stopword.txt"));
		String line = null;
		while ((line = br.readLine()) != null) {
			stopWordList.add(line);
		}
		br.close();
	}

	public static void main(String[] args) throws IOException {
		StopWordRemover filter = new StopWordRemover();
		
	}

}
