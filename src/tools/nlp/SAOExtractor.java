package tools.nlp;

import item.SAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import tools.data.DBManager;
import util.MakeInstrumentationUtil;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOExtractor {
	private static SAOExtractor instance = null;
	private StanfordUtil stanford;
	private GrammaticalStructureFactory gsf;
	private final int MAX_LENGTH_OF_SENTENCE = 30;
	private String WORD_TYPE = "simple";

	// singleton pattern
	public static SAOExtractor getInstance() {
		if (instance == null) {
			instance = new SAOExtractor();
		}
		return instance;
	}

	// constructor
	public SAOExtractor() {
		stanford = StanfordUtil.getInstance();
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
	}

	private String getName(TypedDependency td) {
		return td.reln().getShortName();
	}

	// get all sao list in a paragraph
	public List<SAO> getSAOTupleList(String paragraph) throws IOException {
		List<String> sentList = splitParagraph(paragraph);
		List<SAO> tupleList = new ArrayList<SAO>();

		// logger.debug("Found sentences : " + sentList.size());
		int count = 1;
		for (String sent : sentList) {
			// logger.debug("Extract sentence : " + count++ + " of " +
			// sentList.size());
			tupleList.addAll(getSAOTupleListBySentence(sent));
		}
		// logger.debug("Found SAO tuple : " + tupleList.size());
		return tupleList;
	}

	// split a paragraph into several sentence
	private List<String> splitParagraph(String paragraph) {
		List<String> sentenceList = new ArrayList<String>();
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			StringBuilder sentenceSb = new StringBuilder();
			List<HasWord> sentence = it.next();
			if (sentence.size() > MAX_LENGTH_OF_SENTENCE)
				continue;
			for (HasWord token : sentence) {
				if (sentenceSb.length() > 1) {
					sentenceSb.append(" ");
				}
				sentenceSb.append(token);
			}
			sentenceList.add(sentenceSb.toString());
		}
		return sentenceList;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		SAOExtractor extractor = SAOExtractor.getInstance();
		String s = "The third main drive shaft 21 is rotatably supported by bearings 27 and 28 and provided with a hypoid pinion 30 formed at the front end thereof .";

		String reg = "(\\s[0-9]*[1-9][0-9]*[a-zA-Z]?\\s)(,\\s[0-9]*[1-9][0-9]*[a-zA-Z]?\\s)*((and|or)\\s[0-9]*[1-9][0-9]*[a-zA-Z]?\\s)*";
		s = s.replaceAll(reg, "");
		extractor.getSAOTupleListBySentence(s);
		mgr.close();
	}

	private String regexSentence(String sent) {
		String reg = "((\\d+\\w?\\s?,\\s?)*(\\d+\\w?\\s?(and|or)\\s?\\d+\\w?))|(\\d+\\w?\\s?(and|or)\\s?\\d+\\w?)|(\\d+\\w?\\s)";
		return sent.replaceAll(reg, " ");
		// String reg2 =
		// "((\d+\w?\s?,\s?)*(\d+\w?\s?(and|or)\s?\d+\w?))|(\d\w?+\s?(and|or)\s?\d\w?+)|(\d+\w?)";
	}

	public List<SAO> getSAOTupleListBySentence(String sent) throws IOException {
		List<SAO> list = new ArrayList<SAO>();
		String origin_sent = new String(sent);
		sent = regexSentence(sent);
		Tree parse = stanford.parse(sent);
		List<TypedDependency> subjectTdList = new ArrayList<TypedDependency>();
		List<TypedDependency> objectTdList = new ArrayList<TypedDependency>();
		List<TypedDependency> modifierTdList = new ArrayList<TypedDependency>();
		// System.out.println(gsf.newGrammaticalStructure(parse).typedDependenciesCCprocessed());

		for (TypedDependency td : gsf.newGrammaticalStructure(parse).typedDependenciesCCprocessed()) {
			// System.out.println(td.toString());

			if (isSubjectTd(td)) {
				subjectTdList.add(td);
			} else if (isObjectTd(td)) {
				objectTdList.add(td);
			} else if (isModifierTd(td) && WORD_TYPE.equals("multiple")) {
				modifierTdList.add(td);
			}
		}

		StopWordRemover remover = StopWordRemover.getInstance();

		for (TypedDependency std : subjectTdList) {
			for (TypedDependency otd : objectTdList) {
				if (std.gov().equals(otd.gov())) {
					String subject = std.dep().nodeString();
					String predicate = std.gov().nodeString();
					String object = otd.dep().nodeString();
					// System.out.println(subject + "-" + predicate + "-" + object);

					// search from end to start
					if (!modifierTdList.isEmpty()) {
						for (int i = modifierTdList.size() - 1; i >= 0; i--) {
							TypedDependency mtd = modifierTdList.get(i);
							if (getName(mtd).equals("nn")) {
								if (std.dep().equals(mtd.gov())) {
									subject = mtd.dep().nodeString() + " " + subject;
									// System.out.println(subject + "-" + predicate + "-" +
									// object);
								} else if (otd.dep().equals(mtd.gov())) {
									object = mtd.dep().nodeString() + " " + object;
									// System.out.println(subject + "-" + predicate + "-" +
									// object);
								}
							} else if (getName(mtd).equals("amod")) {
								if (std.dep().equals(mtd.gov())) {
									subject = mtd.dep().nodeString() + " " + subject;
									// System.out.println(subject + "-" + predicate + "-" +
									// object);
								} else if (otd.dep().equals(mtd.gov())) {
									object = mtd.dep().nodeString() + " " + object;
									// System.out.println(subject + "-" + predicate + "-" +
									// object);
								}
							}
						}
					}
					// System.out.println(sent);
					if (remover.matchFilter(subject) || remover.matchFilter(predicate) || remover.matchFilter(object))
						continue;
					
					// use stanford lemmatizer
					subject = stanford.getLemma(subject);
					predicate = stanford.getLemma(predicate);
					object = stanford.getLemma(object);
					
					System.out.println(subject + " <=> " + predicate + " <=> " + object);

					SAO t = new SAO();
					t.set("subject", subject);
					t.set("object", object);
					t.set("predicate", predicate);
					t.set("sentence", origin_sent);
					list.add(t);
				}
			}
		}

		return list;
	}

	private boolean isSubjectTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "nsubj", "xsubj", "agent" });
		String name = getName(td);
		if (list.contains(name)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isObjectTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "dobj", "iobj", "nsubjpass" });
		String name = getName(td);
		if (list.contains(name) || name.contains("prep_")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isModifierTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "nn", "amod" });
		String name = getName(td);
		if (list.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
}