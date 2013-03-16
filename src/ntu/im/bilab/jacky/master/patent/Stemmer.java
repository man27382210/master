package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class Stemmer {
	private static WordnetStemmer stemmer;
	
	public Stemmer() throws IOException {
		String path = "wordnet/3.0/dict";
		URL url = new URL("file", null, path);
		IDictionary dict = new Dictionary(url);
		dict.open();
	  stemmer = new WordnetStemmer(dict);
	  
	}
	
	public String findStems(String word, POS pos) {
		List<String> stems = stemmer.findStems(word, pos);
		if(!stems.isEmpty()) return stems.get(0);
		return null;
	}
}
