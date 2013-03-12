package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.InvalidFormatException;

public class SAOExtractor {
	public static boolean passive_voice = false;

	public static List<SAOTuple> getSAOTuple(String data)
	    throws InvalidFormatException, IOException {
		OpenNLP opennlp = new OpenNLP();
		List<SAOTuple> sao_list = new ArrayList<SAOTuple>();
		List<String> sentences = opennlp.getSentence(data);

		for (String sentence : sentences) {
			Parse root = opennlp.getParse(sentence);
			List<Parse> clauses = getClauses(root);

			for (Parse clause : clauses) {
				Parse subject = getSubject(clause);
				Parse predicate = getPredicate(clause);
				Parse object = getObject(clause, predicate);
				checkPassiveVoice(subject, object);
				if (subject == null || object == null || predicate == null)
					continue;
				sao_list.add(new SAOTuple(clause.toString(), subject.toString(),
				    predicate.toString(), object.toString()));
			}
		}

		return sao_list;
	}

	public String getSubject(String sentence) throws InvalidFormatException, IOException {
		OpenNLP opennlp = new OpenNLP();
		Parse root = opennlp.getParse(sentence);
		return getSubject(root).toString();
	}

	private static void checkPassiveVoice(Parse subject, Parse object) {
		if (passive_voice == true) {
			Parse tmp = null;
			tmp = subject;
			subject = object;
			object = tmp;
			passive_voice = false;
		}
	}

	private static List<Parse> getClauses(Parse tree) {
		List<Parse> clauses = new ArrayList<Parse>();
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(tree);

		while (!queue.isEmpty()) {
			Parse p = queue.poll();
			if (p.getType().equals("S"))
				clauses.add(p);
			queue.addAll(Arrays.asList(p.getChildren()));
		}

		return clauses;
	}

	private static List<Parse> getChildrenByType(Parse tree, String[] types) {
		List<Parse> list = new ArrayList<Parse>();
		for (Parse subtree : tree.getChildren()) {
			for (String type : types) {
				if (subtree.getType().equals(type)) {
					list.add(subtree);
				}
			}
		}
		return list;
	}

	private static List<Parse> getChildrenByType(Parse tree, String type) {
		List<Parse> list = new ArrayList<Parse>();
		for (Parse subtree : tree.getChildren()) {
			if (subtree.getType().equals(type)) {
				list.add(subtree);
			}
		}
		return list;
	}

	private static Parse getSubject(Parse tree) {
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(tree);

		while (!queue.isEmpty()) {
			Parse p = queue.poll();
			List<Parse> list = getChildrenByType(p, "NP");
			if (p.getType().equals("NP") && list.isEmpty()) {
				String[] types = { "NN", "NNP", "NNPS", "NNS" };
				List<Parse> children = getChildrenByType((p), types);
				return children.get(children.size() - 1);
			}
			for (Parse child : list)
				queue.add(child);
		}
		return null;
	}

	private static Parse getPredicate(Parse tree) {
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(tree);

		while (queue.isEmpty() == false) {
			Parse p = queue.poll();
			List<Parse> list = getChildrenByType((p), "VP");
			if (p.getType().equals("VP") && list.isEmpty()) {
				String[] types = { "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" };
				Parse child = getChildrenByType(p, types).get(0);
				if (child.getType().equals("VBN"))
					passive_voice = true;
				return child;
			}
			for (Parse child : list)
				queue.add(child);
		}
		return null;
	}

	private static Parse getObject(Parse tree, Parse predicate) {
		// Parse predicate = getPredicate(tree);
		if (predicate == null)
			return null;
		String[] types = { "NP", "PP", "S" };
		for (Parse p : getSiblingByType(predicate, types)) {
			if (p.getType().equals("NP") || p.getType().equals("PP")) {
				String[] child_type = { "NN", "NNP", "NNPS", "NNS" };
				List<Parse> children = getChildrenByType((p), child_type);
				if (!children.isEmpty()) {
					return children.get(children.size() - 1);
				} else {
					return getSubject(p);
				}
			} else {
				return getSubject(p);
			}
		}
		return null;
	}

	private static List<Parse> getSiblingByType(Parse tree, String[] types) {
		List<Parse> list = new ArrayList<Parse>();
		Parse parent = tree.getParent();
		for (Parse subtree : parent.getChildren()) {
			if (subtree.equals(tree))
				continue;
			for (String type : types) {
				if (subtree.getType().equals(type)) {
					list.add(subtree);
				}
			}
		}
		return list;
	}
}