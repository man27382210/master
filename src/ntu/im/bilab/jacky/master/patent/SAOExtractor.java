package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import opennlp.tools.util.InvalidFormatException;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOExtractor {

	public static String[] getSubject(String sent) {
		StanfordParser parser = StanfordParser.getInstance();
		Tree parse = parser.parse(sent);
		parse.pennPrint();

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		TypedDependency td = tdl.get(0);
		
		System.out.println(td);
		System.out.println(td.toString());
		System.out.println(td.toString(true));
		System.out.println(td.reln().getShortName());
		return null;
	}

	public static void main(String[] args) {
		String s = "My dog also likes sausage.";
		getSubject(s);
	}
}