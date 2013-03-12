package ntu.im.bilab.jacky.master.tools;

import org.tartarus.snowball.ext.EnglishStemmer;

public class Stemmer {
	private EnglishStemmer stemmer;
	
	public Stemmer() {
		stemmer = new EnglishStemmer();
	}

	public String getStem(String value) {
		stemmer.setCurrent(value);
		stemmer.stem();
		return stemmer.getCurrent();
	}
}
