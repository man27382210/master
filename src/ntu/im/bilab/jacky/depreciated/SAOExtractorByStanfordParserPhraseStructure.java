package ntu.im.bilab.jacky.depreciated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ntu.im.bilab.jacky.master.patent.SAOTuple;
import ntu.im.bilab.jacky.master.patent.StanfordParser;
import opennlp.tools.util.InvalidFormatException;
import edu.stanford.nlp.trees.Tree;

public class SAOExtractorByStanfordParserPhraseStructure {
	public List<SAOTuple> getSAOTupleList(String data)
	    throws InvalidFormatException, IOException {
		OpenNLP opennlp = OpenNLP.getInstance();
		StanfordParser parser = StanfordParser.getInstance();

		List<SAOTuple> saoTupleList = new ArrayList<SAOTuple>();
		List<String> sentences = opennlp.getSentence(data);

		for (String sentence : sentences) {
			Tree parse = parser.parse(sentence);
			List<Tree> clauses = getClauses(parse);
			for (Tree clause : clauses) {
				// clause.pennPrint();
				System.out.println(clause.toString());
				// pennString(clause);
				SAOTuple tuple = getSAOTuple(clause);
				if (tuple != null)
					saoTupleList.add(tuple);
			}

		}
		return saoTupleList;
	}

	public SAOTuple getSAOTuple(Tree clause) {
		Tree subject = getSubject(clause);
		if (subject == null)
			return null;
		Tree predicate = getPredicate(clause);
		if (predicate == null)
			return null;
		Tree object = getObject(clause, predicate);
		if (object == null)
			return null;
		checkPassiveVoice(predicate, subject, object);
		return new SAOTuple(getText(clause), getText(subject), getText(predicate),
		    getText(object));
	}

	private String getText(Tree tree) {
		return tree.getLeaves().get(0).toString();
	}

	private void checkPassiveVoice(Tree predicate, Tree subject, Tree object) {
		// if (predicate.getType().equals("VBN")) {
		if (getType(predicate, "VBN")) {
			Tree tmp = null;
			tmp = subject;
			subject = object;
			object = tmp;
		}
	}

	private String getType(Tree tree) {
		String nodeString = tree.nodeString();
		String[] tmp = nodeString.split(" ");
		return tmp[0];
	}

	private boolean getType(Tree tree, String type) {
		return getType(tree).equals(type);
	}

	private boolean getType(Tree tree, String[] types) {
		for (String type : types) {
			if (getType(tree).equals(type)) return true;
		}
		return false;
	}
	
	private List<Tree> getClauses(Tree tree) {
		List<Tree> clauses = new ArrayList<Tree>();
		Queue<Tree> queue = new LinkedList<Tree>();
		queue.add(tree);

		while (!queue.isEmpty()) {
			Tree p = queue.poll();
			if (getType(p, "S"))
				clauses.add(p);
			// queue.addAll(Arrays.asList(p.getChildren()));
			queue.addAll(p.getChildrenAsList());
		}

		return clauses;
	}

	private List<Tree> getChildrenByType(Tree tree, String[] types) {
		List<Tree> list = new ArrayList<Tree>();
		for (Tree subtree : tree.getChildrenAsList()) {
			for (String type : types) {
				if (getType(subtree, type)) {
					list.add(subtree);
				}
			}
		}
		return list;
	}

	private List<Tree> getChildrenByType(Tree tree, String type) {
		List<Tree> list = new ArrayList<Tree>();
		for (Tree subtree : tree.getChildrenAsList()) {
			if (getType(subtree, type)) {
				list.add(subtree);
			}
		}
		return list;
	}

	public String getSubject(String sentence) {
		StanfordParser parser = StanfordParser.getInstance();
		Tree tree = parser.parse(sentence);
		List<Tree> clauses = getClauses(tree);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getSubject();
		} else {
			return null;
		}
	}

	// find the subject from Tree
	private Tree getSubject(Tree tree) {
		String[] types1 = { "NP", "PP" };
		String[] types2 = { "NN", "NNP", "NNPS", "NNS" };
		
		Queue<Tree> queue = new LinkedList<Tree>();
		queue.add(tree);

		while (!queue.isEmpty()) {
			Tree p = queue.poll();
			List<Tree> list = getChildrenByType(p, types1);
			if (getType(p,types1) && list.isEmpty()) {
				List<Tree> children = getChildrenByType((p), types2);
				if (!children.isEmpty()) {
					return children.get(children.size() - 1);
				} else {
					return null;
				}
			}
			for (Tree child : list)
				queue.add(child);
		}
		return null;
	}

	public String getPredicate(String sentence) {
		StanfordParser parser = StanfordParser.getInstance();
		Tree tree = parser.parse(sentence);
		List<Tree> clauses = getClauses(tree);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getPredicate();
		} else {
			return null;
		}
	}

	private Tree getPredicate(Tree tree) {
		Queue<Tree> queue = new LinkedList<Tree>();
		queue.add(tree);

		while (queue.isEmpty() == false) {
			Tree p = queue.poll();
			List<Tree> list = getChildrenByType((p), "VP");
			if (getType(p, "VP") && list.isEmpty()) {
				String[] types = { "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" };
				return getChildrenByType(p, types).get(0);
			}
			for (Tree child : list)
				queue.add(child);
		}
		return null;
	}

	public String getObject(String sentence) {
		StanfordParser parser = StanfordParser.getInstance();
		Tree tree = parser.parse(sentence);
		List<Tree> clauses = getClauses(tree);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getObject();
		} else {
			return null;
		}
	}

	private Tree getObject(Tree tree, Tree predicate) {

		if (predicate == null)
			return null;
		String[] types = { "NP", "PP", "S" };
		for (Tree p : getSiblingByType(tree, predicate, types)) {
			if (getType(p, "NP") || getType(p, "PP")) {
				String[] child_type = { "NN", "NNP", "NNPS", "NNS" };
				List<Tree> children = getChildrenByType((p), child_type);
				if (!children.isEmpty()) {
					return children.get(children.size() - 1);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	private List<Tree> getSiblingByType(Tree root, Tree tree, String[] types) {
		List<Tree> list = new ArrayList<Tree>();

		Tree parent = tree.parent(root);
		for (Tree subtree : parent.getChildrenAsList()) {
			if (subtree.equals(tree))
				continue;
			for (String type : types) {
				if (getType(subtree, type)) {
					list.add(subtree);
				}
			}
		}
		return list;
	}
}