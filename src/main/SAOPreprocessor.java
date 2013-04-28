package main;

import item.Patent;
import item.StanfordTree;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.data.DBManager;
import tools.data.DataSetLoader;
import tools.nlp.SAOExtractor;
import tools.nlp.StanfordUtil;
import util.MakeInstrumentationUtil;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;

public class SAOPreprocessor {
	private static int MAX_LENGTH_OF_SENTENCE = 999999;
	private static int MIN_LENGTH_OF_SENTENCE = 3;
	private static StanfordUtil stanford = StanfordUtil.getInstance();

	// save tree
	public static void parseTree(List<Patent> patentList) {
		for (Patent p : patentList)
			parseTree(p);
	}

	// save tree into
	private static void parseTree(Patent p) {
		String id = p.getString("patent_id");

		for (String str : splitParagraph(p.getString("abstract")))
			saveTree(id, str, "abstract");

		for (String str : splitParagraph(p.getString("claims")))
			saveTree(id, str, "claims");

		for (String str : splitParagraph(p.getString("description")))
			saveTree(id, str, "description");

	}

	// save tree into db
	private static void saveTree(String id, String origin_sent, String section) {
		String sent = removeCode(origin_sent);
		int length = getLength(sent);
		if (length > MAX_LENGTH_OF_SENTENCE || length <MIN_LENGTH_OF_SENTENCE) return;
		Tree tree = stanford.parse(sent);

		StanfordTree t = new StanfordTree();
		t.set("patent_id", id);
		t.set("section", section);
		t.set("tree", tree.toString());
		t.set("length", length);
		t.set("sentence", sent);
		t.set("origin_sentence", origin_sent);
		t.insert();
	}

	private static int getLength(String sent) {
		String[] words = sent.split(" ");
		return words.length;
	}

	private static String removeCode(String sent) {
		String regex = "((\\d+\\w?\\s?,\\s?)*(\\d+\\w?\\s?(and|or)\\s?\\d+\\w?))|(\\d+\\w?\\s?(and|or)\\s?\\d+\\w?)|(\\d+\\w?\\s)";
		return sent.replaceAll(regex, " ");
	}

	// split a paragraph into several sentence
	public static List<String> splitParagraph(String paragraph) {
		List<String> sentenceList = new ArrayList<String>();
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			StringBuilder sentenceSb = new StringBuilder();
			List<HasWord> sentence = it.next();
			for (HasWord token : sentence) {
				System.out.println(token);
				if (sentenceSb.length() > 0) {
					sentenceSb.append(" ");
				}
				sentenceSb.append(token);
			}
			sentenceList.add(sentenceSb.toString());
		}
		return sentenceList;
	}

	public static void main(String[] args) {
		try {
			MakeInstrumentationUtil.make();
			DBManager mgr = DBManager.getInstance();
			mgr.open();
//	    List<String> ids = DataSetLoader.loadID("doc/dataset1.txt");
//	    List<Patent> patents = DataSetLoader.loadPatent(ids);
//	    patents = patents.subList(0,1);
      //	    SAOPreprocessor.parseTree(patents);
	   
	    mgr.close();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
	
}
