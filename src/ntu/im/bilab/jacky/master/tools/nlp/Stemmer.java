package ntu.im.bilab.jacky.master.tools.nlp;

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
	
	public static void main(String[] args) {
		Stemmer stemmer = new Stemmer();
		String str =  stemmer.getStem("accommodated");
		System.out.println(str);
	}
}