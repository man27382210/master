package tools.nlp;

import item.Patent;
import item.SAO;
import item.StanfordTree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import tools.data.DBManager;
import tools.data.DataSetLoader;
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
	private static StanfordUtil stanford;
	private static GrammaticalStructureFactory gsf;
	private static String WORD_TYPE = "simple";

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

	private static String getName(TypedDependency td) {
		return td.reln().getShortName();
	}

	public static List<SAO> extractSAO(Patent p) throws IOException {
		List<SAO> saoList = new ArrayList<SAO>();
		String id = p.getString("patent_id");
		List<StanfordTree> list = StanfordTree.findAll();
		for (StanfordTree t : list) {
			Tree tree = Tree.valueOf(t.getString("tree"));
			saoList.addAll(extractSAO(tree, t.getString("sentence")));
		}
		
		for (SAO sao : saoList) {
			System.out.println(sao.toString());
		}
		return saoList;
	}

	public static void main(String[] args) throws Exception {
		MakeInstrumentationUtil.make();
		DBManager mgr = DBManager.getInstance();
		mgr.open();
		List<String> ids = DataSetLoader.loadID("doc/dataset1.txt");
    List<Patent> patents = DataSetLoader.loadPatent(ids);
    patents = patents.subList(0,1);
		//SAOExtractor.extractSAO(patents.get(0));
		mgr.close();
	}

	public static List<SAO> extractSAO(Tree parse, String sent) throws IOException {
		List<SAO> list = new ArrayList<SAO>();

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

				}
			}
		}

		return list;
	}

	private static boolean isSubjectTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "nsubj", "xsubj", "agent" });
		String name = getName(td);
		if (list.contains(name)) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isObjectTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "dobj", "iobj", "nsubjpass" });
		String name = getName(td);
		if (list.contains(name) || name.contains("prep_")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isModifierTd(TypedDependency td) {
		List<String> list = Arrays.asList(new String[] { "nn", "amod" });
		String name = getName(td);
		if (list.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
}