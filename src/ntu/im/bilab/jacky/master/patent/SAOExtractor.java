package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.InvalidFormatException;

public class SAOExtractor {
	private Parse predicate_in_vp = null;

	public static List<SAOTuple> getSAOTuple(String data)
	    throws InvalidFormatException, IOException {
		List<SAOTuple> sao_list = new ArrayList<SAOTuple>();
		List<String> sentences = Arrays.asList(OpenNLP.getSentence(data));

		for (String sentence : sentences) {
			Parse root = OpenNLP.getParse(sentence);
			List<Parse> clauses = getClauses(root);

			for (Parse clause : clauses) {
				Parse subject = getSubject(clause);
				Parse predicate = getPredicate(clause);
				Parse object = getObject(clause, predicate);
				if (subject == null || object == null || predicate == null)
					continue;
				sao_list.add(new SAOTuple(clause.toString(), subject.toString(),
				    predicate.toString(), object.toString()));
			}
		}

		return sao_list;
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

	@SuppressWarnings("unused")
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
				return getChildrenByType((p), types).get(0);
			}
			for (Parse child : list)
				queue.add(child);
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

	private static Parse getObject(Parse tree, Parse predicate) {
		// Parse predicate = getPredicate(tree);
		if (predicate == null)
			return null;
		String[] types = { "NP", "PP", "ADJP", "S" };
		for (Parse p : getSiblingByType(predicate, types)) {
			if (p.getType().equals("NP") || p.getType().equals("PP")) {
				String[] child_type = { "NN", "NNP", "NNPS", "NNS" };
				List<Parse> children = getChildrenByType((p), child_type);
				if (!children.isEmpty()) {
					System.out.println(1);
					return children.get(children.size() - 1);
				} else {
					System.out.println(2);
					return p;
				}
			} else {
				System.out.println(3);
				return p;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private String Old_getSubject(Parse tree) {
		Queue<Parse> q = new LinkedList<Parse>();
		q.add(tree);

		while (q.isEmpty() == false) {
			Parse p = q.poll();
			String type = p.getType();
			if (type.equals("NN") || type.equals("NNP") || type.equals("NNPS")
			    || type.equals("NNS"))
				return p.toString();
			else {
				for (Parse subtree : p.getChildren()) {
					q.add(subtree);
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unused")
	private String Old_getPredicate(Parse tree) {
		Queue<Parse> q = new LinkedList<Parse>();
		Map<Parse, Integer> map = new HashMap<Parse, Integer>();

		int depth = 0;
		q.add(tree);
		map.put(tree, depth);

		while (q.isEmpty() == false) {
			Parse p = q.poll();
			for (Parse subtree : p.getChildren()) {
				q.add(subtree);
				map.put(subtree, map.get(subtree.getParent()) + 1);
			}
		}

		Parse deepestVP = null;
		int deepestVPDepth = 0;

		for (Parse p : map.keySet()) {
			int value = map.get(p);
			String type = p.getType();

			if (value > deepestVPDepth) {
				if (type.equals("VB") || type.equals("VBD") || type.equals("VBG")
				    || type.equals("VBN") || type.equals("VBP") || type.equals("VBZ")) {
					deepestVP = p;
					deepestVPDepth = value;
				}
			}
		}

		if (deepestVP != null) {
			predicate_in_vp = deepestVP.getParent();
			return deepestVP.toString();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private String Old_getObject(Parse tree) {
		Parse[] siblings = predicate_in_vp.getChildren();
		for (Parse sibling : siblings) {
			String type = sibling.getType();
			if (type.equals("VP")) {
				continue;
			} else if (type.equals("NP") || type.equals("PP")) {
				Queue<Parse> q = new LinkedList<Parse>();
				q.add(sibling);

				while (q.isEmpty() == false) {
					Parse p = q.poll();
					String s = p.getType();
					if (s.equals("NN") || s.equals("NNP") || s.equals("NNPS")
					    || s.equals("NNS")) {
						return p.toString();
					} else {
						for (Parse subtree : p.getChildren()) {
							q.add(subtree);
						}
					}
				}
			}
		}
		return null;
	}

}
