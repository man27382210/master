package main;

import item.Patent;
import item.StanfordTree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.data.DBManager;
import tools.data.DataSetLoader;
import tools.nlp.StanfordUtil;
import util.MakeInstrumentationUtil;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOPreprocessor {
  private static int MAX_LENGTH_OF_SENTENCE = 60;
  private static int MIN_LENGTH_OF_SENTENCE = 4;
  private static StanfordUtil stanford = StanfordUtil.getInstance();
  private static GrammaticalStructureFactory gsf = new PennTreebankLanguagePack().grammaticalStructureFactory();

  // save tree
  public static void parseTree(List<Patent> patentList) throws IOException {
    for (Patent p : patentList) {
      parseTree(p);
      System.out.println("complete parsing tree ... patent id : " + p.getString("patent_id"));
    }
  }

  // save tree into
  private static void parseTree(Patent p) throws IOException {
    String id = p.getString("patent_id");
    long count = StanfordTree.count("patent_id = ?" , id);
    if (count > 0) {
      System.out.println(id + " is in db!!!");
      return;
    } else {
      
      for (String str : splitParagraph(p.getString("abstract")))
        saveTree(id, str, "abstract");

      for (String str : splitParagraph(p.getString("claims")))
        saveTree(id, str, "claims");

      for (String str : splitParagraph(p.getString("description")))
        saveTree(id, str, "description");
      
    }
   
  }

  // save tree into db
  private static void saveTree(String id, String origin_sent, String section) throws IOException {
    String sent = removeCode(origin_sent);
    int length = getLength(sent);
    if (length > MAX_LENGTH_OF_SENTENCE || length < MIN_LENGTH_OF_SENTENCE)
      return;
    
    long d0 = System.currentTimeMillis();
    Tree tree = stanford.parse(sent);
    List<TypedDependency> tdList = gsf.newGrammaticalStructure(tree).typedDependenciesCCprocessed();
    long d1 = System.currentTimeMillis();
    
    // write object into byte type
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(tdList);
    byte[] treeAsBytes = baos.toByteArray();

    System.out.println("insert a tree ...");

    StanfordTree t = new StanfordTree();
    t.set("patent_id", id);
    t.set("section", section);
    t.set("tree", tree.toString());
    t.set("length", length);
    t.set("parse_time", (d1-d0));
    t.set("sentence", sent);
    t.set("origin_sentence", origin_sent);
    t.set("tree_info", treeAsBytes);
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
        // System.out.println(token);
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

      List<String> ids = DataSetLoader.loadID("doc/dataset1.txt");
      List<Patent> patents = DataSetLoader.loadPatent(ids);

      // truncate tree table
//      StanfordTree.deleteAll();
//      SAOPreprocessor.parseTree(patents);

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
