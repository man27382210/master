package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOExtractor {
	private List<String> subjectTd;
	private List<String> objectTd;
	private StanfordParser parser;
	private GrammaticalStructureFactory gsf;

	public SAOExtractor() {
		parser = StanfordParser.getInstance();
		gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();
		subjectTd = Arrays
				.asList(new String[] { "nsubj", "nsubjpass", "xsubj" });
		objectTd = Arrays.asList(new String[] { "dobj", "iobject", "pobj",
				"prep", "prep_in" });
	}

	public List<SAOTuple> getSAOTupleList(String sent) {
		List<SAOTuple> tupleList = new ArrayList<SAOTuple>();
		Tree parse = parser.parse(sent);
		List<TypedDependency> tdl = gsf.newGrammaticalStructure(parse)
				.typedDependenciesCCprocessed();

		for (TypedDependency td : tdl) {
			// get subject
			if (subjectTd.contains(getName(td))) {
				TreeGraphNode subject = td.dep();
				TreeGraphNode predicate = td.gov();

				for (TypedDependency td2 : tdl) {

					if (objectTd.contains(getName(td2))) {
						TreeGraphNode object = td2.dep();
						TreeGraphNode predicate2 = td2.gov();

						if (predicate.equals(predicate2)) {

							SAOTuple tuple = new SAOTuple("sent",
									subject.nodeString(),
									predicate.nodeString(), object.nodeString());
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
		System.out.println(tdl.toString());
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

	public static void main(String[] args) {
		String s = "Bell, a company which is based in LA, makes and distributes computer products.";
		s = "A temporary indicated torque is obtained by taking a conventional dead " +
				"zone area for a first slip control area, and the value proportional to the " +
				"slip quantity for a maximum value, this temporary indicated torque is corrected " +
				"by a correction value according to the tight cornering brake quantity to be the " +
				"indicated torque of the transfer clutch, and occurrence of any tight cornering" +
				" brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		s = "In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		SAOExtractor saoe = new SAOExtractor();
		
		System.out.println(saoe.getSAOTupleList(s));
	}
}