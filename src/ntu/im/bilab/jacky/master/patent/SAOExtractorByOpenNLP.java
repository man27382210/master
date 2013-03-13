package ntu.im.bilab.jacky.master.patent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.InvalidFormatException;

public class SAOExtractorByOpenNLP {
	public List<SAOTuple> getSAOTupleList(String data)
	    throws InvalidFormatException, IOException {
		OpenNLP opennlp = OpenNLP.getInstance();
		List<SAOTuple> saoTupleList = new ArrayList<SAOTuple>();
		List<String> sentences = opennlp.getSentence(data);

		for (String sentence : sentences) {
			List<Parse> parseList = opennlp.getParseList(sentence);
			for (Parse parse : parseList) {
				List<Parse> clauses = getClauses(parse);
				for (Parse clause : clauses) {
					clause.show();
					System.out.println(clause.toString());
					// pennString(clause);
					SAOTuple tuple = getSAOTuple(clause);
					if (tuple != null)
						saoTupleList.add(tuple);
				}
			}
		}
		return saoTupleList;
	}

	public void pennString(Parse parse) {
		StringBuffer sb = new StringBuffer(parse.toString().length() * 4);
		StringBuffer space = new StringBuffer(parse.toString().length() * 4);
		System.out.print(pennString(parse, sb, space));
	}

	private StringBuffer pennString(Parse parse, StringBuffer sb,
	    StringBuffer space) {
		String types[] = { "NP", "VP", "ADVP", "PP" };
		List<String> typeList = Arrays.asList(types);
		if (!getChildrenByType(parse, "TK").isEmpty()) {
			sb.append("(");
			sb.append(parse.getType());
			sb.append(" ");
			sb.append(parse.toString());
			sb.append(")");
			if (parse.getParent().indexOf(parse) != parse.getParent().getChildCount() - 1)
				sb.append(" ");
		} else if (parse.getType().equals("S")) {
			sb.append("\n");
			sb.append(space);
			sb.append("(");
			sb.append(parse.getType());
			space.append("  ");
			sb.append(space);
			for (Parse child : Arrays.asList(parse.getChildren())) {
				pennString(child, sb, space);
			}
		} else if (typeList.contains(parse.getType())) {
			if (typeList.contains(parse.getParent().getType())) {
				space.append("  ");
			}
			sb.append("\n");
			sb.append(space);
			sb.append("(");
			sb.append(parse.getType());
			sb.append(" ");

			for (Parse child : Arrays.asList(parse.getChildren())) {
				pennString(child, sb, space);
			}
			sb.append(")");
		} else {

		}
		return sb;
	}

	public SAOTuple getSAOTuple(Parse clause) {
		Parse subject = getSubject(clause);
		if (subject == null)
			return null;
		Parse predicate = getPredicate(clause);
		if (predicate == null)
			return null;
		Parse object = getObject(clause, predicate);
		if (object == null)
			return null;
		checkPassiveVoice(predicate, subject, object);
		return new SAOTuple(clause.toString(), subject.toString(),
		    predicate.toString(), object.toString());
	}

	private void checkPassiveVoice(Parse predicate, Parse subject, Parse object) {
		if (predicate.getType().equals("VBN")) {
			Parse tmp = null;
			tmp = subject;
			subject = object;
			object = tmp;
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

	public String getSubject(String sentence) throws InvalidFormatException,
	    IOException {
		OpenNLP opennlp = OpenNLP.getInstance();
		Parse parse = opennlp.getParse(sentence);
		List<Parse> clauses = getClauses(parse);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getSubject();
		} else {
			return null;
		}
	}

	// find the subject from parse
	private static Parse getSubject(Parse parse) {
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(parse);

		while (!queue.isEmpty()) {
			Parse p = queue.poll();
			List<Parse> list = getChildrenByType(p, "NP");
			if (p.getType().equals("NP") && list.isEmpty()) {
				String[] types = { "NN", "NNP", "NNPS", "NNS" };
				List<Parse> children = getChildrenByType((p), types);
				if (!children.isEmpty()) {
					return children.get(children.size() - 1);
				} else {
					return null;
				}
			}
			for (Parse child : list)
				queue.add(child);
		}
		return null;
	}

	public String getPredicate(String sentence) throws InvalidFormatException,
	    IOException {
		OpenNLP opennlp = OpenNLP.getInstance();
		Parse parse = opennlp.getParse(sentence);
		List<Parse> clauses = getClauses(parse);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getPredicate();
		} else {
			return null;
		}
	}

	private static Parse getPredicate(Parse tree) {
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(tree);

		while (queue.isEmpty() == false) {
			Parse p = queue.poll();
			List<Parse> list = getChildrenByType((p), "VP");
			if (p.getType().equals("VP") && list.isEmpty()) {
				String[] types = { "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" };
				return getChildrenByType(p, types).get(0);
			}
			for (Parse child : list)
				queue.add(child);
		}
		return null;
	}

	public String getObject(String sentence) throws InvalidFormatException,
	    IOException {
		OpenNLP opennlp = OpenNLP.getInstance();
		Parse parse = opennlp.getParse(sentence);
		List<Parse> clauses = getClauses(parse);
		SAOTuple tuple = getSAOTuple(clauses.get(0));
		if (tuple != null) {
			return tuple.getObject();
		} else {
			return null;
		}
	}

	private static Parse getObject(Parse tree, Parse predicate) {
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
					return null;
				}
			} else {
				return null;
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