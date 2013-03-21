package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import opennlp.tools.util.InvalidFormatException;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOExtractor {
	private static SAOExtractor instance = null;
	private List<String> subjectTd;
	private List<String> objectTd;
	private StanfordParser parser;
	private GrammaticalStructureFactory gsf;
	private final int MAX_LENGTH_OF_SENTENCE = 10;

	// singleton
	public static SAOExtractor getInstance() {
		if (instance == null) {
			instance = new SAOExtractor();
		}
		return instance;
	}
	
	public SAOExtractor() {
		parser = StanfordParser.getInstance();
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
		subjectTd = Arrays.asList(new String[] { "nsubj", "nsubjpass", "xsubj" });
		objectTd = Arrays
		    .asList(new String[] { "dobj", "iobject", "pobj", "prep" });
	}

	public List<SAOTuple> getSAO(String paragraph) {
		List<String> sentList = new ArrayList<String>();
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();

		// convert data into splited sentence
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			StringBuilder sentenceSb = new StringBuilder();
			List<HasWord> sentence = it.next();
			if (sentence.size() > MAX_LENGTH_OF_SENTENCE ) continue;
			for (HasWord token : sentence) {
				if (sentenceSb.length() > 1) {
					sentenceSb.append(" ");
				}
				sentenceSb.append(token);
			}
			sentList.add(sentenceSb.toString());
		}

		System.out.println("Found sentences : " + sentList.size());

		int count = 1;
		// add tuple list
		for (String sent : sentList) {
			//System.out.println(sent);
			System.out.println("Extract sentence : " + count++
			    + " of " + sentList.size());
			tupleList.addAll(getSAOTupleList(sent));
		}

		System.out.println("Found SAO tuple : " + tupleList.size());
		return tupleList;
	}

	public List<SAOTuple> getSAOTupleList(String sent) {
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();
		Tree parse = parser.parse(sent);
		List<TypedDependency> tdl = gsf.newGrammaticalStructure(parse)
		    .typedDependenciesCCprocessed();

		for (TypedDependency td : tdl) {
			// System.out.println(td.toString());
			// get subject
			if (subjectTd.contains(getName(td))) {
				TreeGraphNode subject = td.dep();
				TreeGraphNode predicate = td.gov();

				for (TypedDependency td2 : tdl) {

					if (objectTd.contains(getName(td2))) {
						TreeGraphNode object = td2.dep();
						TreeGraphNode predicate2 = td2.gov();

						if (predicate.equals(predicate2)) {

							SAOTuple tuple = new SAOTuple("sent", subject.nodeString().toLowerCase(),
							    predicate.nodeString().toLowerCase(), object.nodeString().toLowerCase());
							tupleList.add(tuple);
						}
					}
				}

			}

		}

		return tupleList;
	}

	public List<String> getSubjectList(String sent) {
		List<String> subjectList = new ArrayList<String>();
		Tree parse = parser.parse(sent);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

		for (TypedDependency td : tdl) {
			if (subjectTd.contains(getName(td)))
				subjectList.add(getDep(td));
		}

		return subjectList;
	}

	public List<String> getObjectList(String sent) {
		List<String> list = new ArrayList<String>();
		Tree parse = parser.parse(sent);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		for (TypedDependency td : tdl) {
			if (objectTd.contains(getName(td)))
				if (!list.contains(getDep(td)))
					list.add(getDep(td));
		}

		return list;
	}

	private String getDep(TypedDependency td) {
		return td.dep().nodeString();
	}

	private String getGov(TypedDependency td) {
		return td.gov().nodeString();
	}

	private String getName(TypedDependency td) {
		return td.reln().getShortName();
	}

	// public static void main(String[] args) {
	// String s =
	// "Bell, a company which is based in LA, makes and distributes computer products.";
	// s = "My dog and cat who likes sausage and suger.";
	//
	// SAOExtractor saoe = new SAOExtractor();
	// System.out.println(saoe.getSAOTupleList(s));
	// }
}