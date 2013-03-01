package ntu.im.bilab.jacky.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import opennlp.tools.parser.Parse;

public class SAOExtractor {
	private Parse predicate_in_vp = null;

	public static void main(String[] args) {
		String data5 = "In various embodiments, an online multiplayer "
		    + "game can provide a mechanism for player characters "
		    + "to acquire in-game assets.";
		String data3 = "A rare black squirrel has become a regular visitor to a suburban garden";
		String data1 = "A temporary indicated torque is obtained by taking a conventional dead zone area for a first slip control area, and the value proportional to the slip quantity for a maximum value, this temporary indicated torque is corrected by a correction value according to the tight cornering brake quantity to be the indicated torque of the transfer clutch, and occurrence of any tight cornering brake phenomenon is prevented thereby. In a slip control area after passing a dead zone area (a second slip control area), the slip control is smoothly transferred from the first slip control area to the second slip control area by performing the slip control with a value of the indicated torque according to the slip quantity added to the indicated torque in the first slip control area as the indicated torque, abrupt torque change is prevented, and the vehicle behavior is stabilized thereby.";
		String data = "A slip control device of a four-wheel-drive vehicle to prevent any slip of wheels by varying the torque transmission distribution to a front wheel side and a rear wheel side via a transfer clutch, and controlling the coupling force of said transfer clutch when the wheels slip, said device comprising: means for calculating an indicated value to the coupling force of said transfer clutch in a first area in which a wheel slip quantity is not exceeding a preset value; means for correcting the indicated value to the coupling force of said transfer clutch in said first area by a correction value according to a tight cornering brake quantity; and means for calculating the indicated value to the coupling force of said transfer clutch when transferring to a second area in which the wheel slip quantity exceeds the preset value from said first area as a value of the indicated value in said first area added to the indicated value according to the slip quantity in said second area."; 
		
		OpenNLPTester opennlp = new OpenNLPTester();
		SAOExtractor saoe = new SAOExtractor();
		String[] sentences = opennlp.getSentence(data);
		for (String s : sentences) {
			saoe.getSAOTriple(opennlp, s);
		}
	}

	protected String getSAOTriple(OpenNLPTester opennlp, String sentence) {
		String sao = null;
		Parse root = opennlp.getParse(sentence);
		List<Parse> clauses = getClauses(root);
		
		System.out.println("Sentence : " + sentence);
		System.out.println("==========");
		for (Parse clause : clauses) {
			
			Parse subject = getSubject(clause);
			Parse predicate = getPredicate(clause);
			Parse object = getObject(clause,predicate);
			
			if (subject == null || object == null || predicate == null) continue;
			System.out.println("Clause : " + clause);
			System.out.println("Subject : " + subject);
			System.out.println("Predicate : " + predicate);
			System.out.println("Object : " + object);
			System.out.println("----------");
		}

		return sao;
	}

	private List<Parse> getClauses(Parse tree) {
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
	private List<Parse> getChildrenByType(Parse tree, String[] types) {
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

	private List<Parse> getChildrenByType(Parse tree, String type) {
		List<Parse> list = new ArrayList<Parse>();
		for (Parse subtree : tree.getChildren()) {
			if (subtree.getType().equals(type)) {
				list.add(subtree);
			}
		}
		return list;
	}

	private Parse getSubject(Parse tree) {
		Queue<Parse> queue = new LinkedList<Parse>();
		queue.add(tree);

		while (!queue.isEmpty()) {
			Parse p = queue.poll();
			List<Parse> list = getChildrenByType(p, "NP");
			if (p.getType().equals("NP") && list.isEmpty())
				return p;
			for (Parse child : list)
				queue.add(child);
		}
		return null;
	}

	private Parse getPredicate(Parse tree) {
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

	private List<Parse> getSiblingByType(Parse tree, String[] types) {
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

	private Parse getObject(Parse tree, Parse predicate) {
		//Parse predicate = getPredicate(tree);
		if (predicate == null) return null;
		String[] types = { "NP", "PP", "ADJP" };
		for (Parse p : getSiblingByType(predicate, types)) {
			return p;
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
