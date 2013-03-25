package ntu.im.bilab.jacky.master.tools.nlp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ntu.im.bilab.jacky.master.item.SAOTuple;

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
	
	// singleton
	public static SAOExtractor getInstance() {
		if (instance == null) {
			instance = new SAOExtractor();
		}
		return instance;
	}

	private List<String> subjectTd;
	private List<String> objectTd;
	private StanfordParser parser;
	private GrammaticalStructureFactory gsf;

	private final int MAX_LENGTH_OF_SENTENCE = 30;

	public SAOExtractor() {
		parser = StanfordParser.getInstance();
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
		subjectTd = Arrays.asList(new String[] { "nsubj", "nsubjpass", "xsubj" });
		objectTd = Arrays.asList(new String[] { "dobj", "iobj", "pobj", "prep" });
		logger = Logger.getLogger(this.getClass().getSimpleName());
	}

	private String getDep(TypedDependency td) {
		return td.dep().nodeString();
	}

	private String getName(TypedDependency td) {
		return td.reln().getShortName();
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

	public List<SAOTuple> getSAOTupleList(String paragraph) throws IOException {
		List<String> sentList = splitParagraph(paragraph);
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();

		//System.out.println("Found sentences : " + sentList.size());
		logger.debug("Found sentences : " + sentList.size());
		int count = 1;
		// add tuple list
		for (String sent : sentList) {
			// System.out.println(sent);
			//System.out.println("Extract sentence : " + count++ + " of  + sentList.size());
			logger.debug("Extract sentence : " + count++ + " of " + sentList.size());
			tupleList.addAll(getSAOTupleListBySentence(sent));
		}
		logger.debug("Found SAO tuple : " + tupleList.size());
		//System.out.println("Found SAO tuple : " + tupleList.size());
		return tupleList;
	}

	private List<String> splitParagraph(String paragraph) {
		List<String> sentList = new ArrayList<String>();
		// convert data into splited sentence
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
			sentList.add(sentenceSb.toString());
		}
		return sentList;
	}

	private List<SAOTuple> getSAOTupleListBySentence(String sent) throws IOException {
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();
		Tree parse = parser.parse(sent);
		List<TypedDependency> tdl = gsf.newGrammaticalStructure(parse)
		    .typedDependenciesCCprocessed();

		
		SAOFilter filter = SAOFilter.getInstance();
		for (TypedDependency td : tdl) {
			// System.out.println(td.toString());
			// get subject
			if (subjectTd.contains(getName(td))) {
				TreeGraphNode subject = td.dep();
				TreeGraphNode predicate = td.gov();
				
				if(filter.matchFilter(subject.nodeString().toLowerCase())) continue;
				if(filter.matchFilter(predicate.nodeString().toLowerCase())) continue;
				
				for (TypedDependency td2 : tdl) {

					if (objectTd.contains(getName(td2))) {
						TreeGraphNode object = td2.dep();
						TreeGraphNode predicate2 = td2.gov();

						if(filter.matchFilter(object.nodeString().toLowerCase())) continue;
						if(filter.matchFilter(predicate2.nodeString().toLowerCase())) continue;
						
						if (predicate.equals(predicate2)) {

							SAOTuple tuple = new SAOTuple(subject.nodeString()
							    .toLowerCase(), predicate.nodeString().toLowerCase(), object
							    .nodeString().toLowerCase());
							logger.debug(tuple.toString());
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
}