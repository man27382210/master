package tools.nlp;

import item.Patent;
import item.SaoTuple;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.PorterStemmer;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public class Stemmer {
	private static Stemmer instance;
	private WordnetStemmer wordnetStemmer;
	
	public static Stemmer getInstance() throws IOException {
		if(instance == null) {
			instance = new Stemmer();
			URL url = new URL("file", null, "wordnet/3.0/dict");
			IDictionary dict = new Dictionary(url);;
			dict.open();
			instance.wordnetStemmer = new WordnetStemmer(dict);
		}
		return instance;
	}

	public String getStem(String word , POS pos) {
		List<String> list = wordnetStemmer.findStems(word, pos);
		if (!list.isEmpty()) {
			return list.get(0);
		}else {
			return word;
		}
	}
	
	public void stem(List<Patent> list){
		for(Patent p : list) {
			stem(p);
		}
	}
	
	public void stem(SaoTuple t) {
		t.setSubject(getStem(t.getSubject(), POS.NOUN));
		t.setPredicate(getStem(t.getPredicate(), POS.VERB));
		t.setObject(getStem(t.getObject(), POS.NOUN));
	}
	
	public void stem(Patent p) {
		List<SaoTuple> list = p.getSaoTupleList();
		for (SaoTuple t : list) {
			stem(t);
		}
	}
	
	public static void main(String[] args) throws IOException {
		URL url = new URL("file", null, "wordnet/3.0/dict");
		
		IDictionary dict = new Dictionary(url);;
		dict.open();
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		String s = stemmer.findStems("distributed", null).get(0);
		System.out.println(s);
	}
}