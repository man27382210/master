package tools.nlp;

import item.SAOTuple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
	private Logger logger;
	private List<String> subjectTd;
	private List<String> objectTd;
	private StanfordParser parser;
	private GrammaticalStructureFactory gsf;
	private final int MAX_LENGTH_OF_SENTENCE = 30;

	// singleton pattern
	public static SAOExtractor getInstance() {
		if (instance == null) {
			instance = new SAOExtractor();
		}
		return instance;
	}

	// constructor
	public SAOExtractor() {
		parser = StanfordParser.getInstance();
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
		subjectTd = Arrays.asList(new String[] { "nsubj", "nsubjpass", "xsubj" });
		objectTd = Arrays.asList(new String[] { "dobj", "iobj", "pobj", "prep" });
		logger = Logger.getLogger(this.getClass().getSimpleName());
	}

	private String getName(TypedDependency td) {
		return td.reln().getShortName();
	}

	// get all sao list in a paragraph
	public List<SAOTuple> getSAOTupleList(String paragraph) throws IOException {
		List<String> sentList = splitParagraph(paragraph);
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();

		logger.debug("Found sentences : " + sentList.size());
		int count = 1;
		for (String sent : sentList) {
			logger.debug("Extract sentence : " + count++ + " of " + sentList.size());
			tupleList.addAll(getSAOTupleListBySentence(sent));
		}
		logger.debug("Found SAO tuple : " + tupleList.size());
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

	// get sao list in a sentence
	private List<SAOTuple> getSAOTupleListBySentence(String sent)
	    throws IOException {
		List<SAOTuple> saoTupleList = new ArrayList<SAOTuple>();
		// create the parse structure
		Tree parse = parser.parse(sent);
		List<TypedDependency> tdl = gsf.newGrammaticalStructure(parse)
		    .typedDependenciesCCprocessed();

		StopWordRemover remover = StopWordRemover.getInstance();
		for (TypedDependency td : tdl) {
			// get dependency match subject relationship
			if (subjectTd.contains(getName(td))) {
				TreeGraphNode subject = td.dep();
				TreeGraphNode predicate = td.gov();
				// match stopword
				if (remover.matchFilter(subject.nodeString())
				    || remover.matchFilter(predicate.nodeString()))
					continue;
				for (TypedDependency td2 : tdl) {
					if (objectTd.contains(getName(td2))) {
						TreeGraphNode object = td2.dep();
						TreeGraphNode predicate2 = td2.gov();
						if (remover.matchFilter(object.nodeString()))
							continue;
						if (predicate.equals(predicate2)) {
							SAOTuple tuple = new SAOTuple(new String(subject.nodeString()),
							    new String(predicate.nodeString()), new String(object.nodeString()));
							logger.debug(tuple.toString());
							saoTupleList.add(tuple);
						}
					}
				}
			}
		}

		return saoTupleList;
	}
}