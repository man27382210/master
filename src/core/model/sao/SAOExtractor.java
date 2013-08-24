package core.model.sao;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import core.dbmodel.MakeInstrumentationUtil;
import core.dbmodel.Patent;
import core.dbmodel.SAO;
import core.dbmodel.StanfordTree;

import tools.data.DBManager;
import tools.data.DataSetLoader;
import tools.nlp.StanfordUtil;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreeReaderFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;

public class SAOExtractor {
  private static SAOExtractor instance = null;
  private static boolean PHRASE_MODE = true;
  private static StanfordUtil stanford = StanfordUtil.getInstance();
  
  // singleton pattern
  public static SAOExtractor getInstance() {
    if (instance == null) {
      instance = new SAOExtractor();
    }
    return instance;
  }

  private static String getName(TypedDependency td) {
    return td.reln().getShortName();
  }
  
  public static void extract(List<Patent> list) throws ClassNotFoundException, IOException {
    int total = 0;
    int max = 0;
    int min = 9999;

    for (Patent p : list) {
      long d0 = System.currentTimeMillis();
      List<SAO> saoList = extractSAO(p);
      long d1 = System.currentTimeMillis();
      p.setSaoTupleList(saoList);
      int count = saoList.size();
      total = total + count;
      if (count > max)
        max = count;
      if (count < min)
        min = count;
      System.out.println(p.getString("patent_id") + " complete extract sao in " + (d1 - d0) + " ms , count:" + saoList.size());
    }

    double avg = (double) total / (double) list.size();

    System.out.println("total : " + total + " avg : " + avg + " max : " + max + " min : " + min);
  }

  public static List<SAO> extractSAO(Patent p) throws IOException, ClassNotFoundException {
    List<SAO> saoList = new ArrayList<SAO>();
    String id = p.getString("patent_id");
    List<StanfordTree> list = StanfordTree.where("patent_id = ?", id);
    for (StanfordTree t : list) {
      String sent = t.getString("sentence");
      byte[] st = t.getBytes("tree_info");
      ByteArrayInputStream baip = new ByteArrayInputStream(st);
      ObjectInputStream ois = new ObjectInputStream(baip);
      List<TypedDependency> tdList = (List<TypedDependency>) ois.readObject();

      saoList.addAll(extractSAO(tdList, id, sent));
    }

    return saoList;
  }

  public static void main(String[] args) throws Exception {
    MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();
    mgr.open();
    List<String> ids = DataSetLoader.loadID("doc/dataset1.txt");
    List<Patent> patents = DataSetLoader.loadPatent(ids);
    // patents = patents.subList(0, 1);
    SAOExtractor.extract(patents);
    mgr.close();
  }

  public static List<SAO> extractSAO(List<TypedDependency> tdList, String id, String sent) throws IOException {
    List<SAO> list = new ArrayList<SAO>();

    List<TypedDependency> subjectTdList = new ArrayList<TypedDependency>();
    List<TypedDependency> objectTdList = new ArrayList<TypedDependency>();
    List<TypedDependency> modifierTdList = new ArrayList<TypedDependency>();

    for (TypedDependency td : tdList) {

      if (isSubjectTd(td)) {
        subjectTdList.add(td);
      } else if (isObjectTd(td)) {
        objectTdList.add(td);
      } else if (isModifierTd(td) && PHRASE_MODE) {
        modifierTdList.add(td);
      }
    }

    for (TypedDependency std : subjectTdList) {
      for (TypedDependency otd : objectTdList) {
        if (std.gov().equals(otd.gov())) {
          String subject = clean(std.dep().nodeString());
          String predicate = clean(std.gov().nodeString());
          String object = clean(otd.dep().nodeString());

          // search from end to start
          if (!modifierTdList.isEmpty()) {
            for (int i = modifierTdList.size() - 1; i >= 0; i--) {
              TypedDependency mtd = modifierTdList.get(i);

              if (getName(mtd).equals("nn")) {
                String word = clean(mtd.dep().nodeString());
                
                if (std.dep().equals(mtd.gov())) {
                  subject = word + " " + subject;

                } else if (otd.dep().equals(mtd.gov())) {
                  object = word + " " + object;

                }
              }              
//              if (getName(mtd).equals("nn")) {
//                if (std.dep().equals(mtd.gov())) {
//                  subject = mtd.dep().nodeString() + " " + subject;
//
//                } else if (otd.dep().equals(mtd.gov())) {
//                  object = mtd.dep().nodeString() + " " + object;
//
//                }
//              } else if (getName(mtd).equals("amod")) {
//                if (std.dep().equals(mtd.gov())) {
//                  subject = mtd.dep().nodeString() + " " + subject;
//
//                } else if (otd.dep().equals(mtd.gov())) {
//                  object = mtd.dep().nodeString() + " " + object;
//
//                }
//              }
            }
          }

          SAO sao = new SAO();
          sao.set("patent_id", id);
          sao.set("subject", subject);
          sao.set("predicate", predicate);
          sao.set("object", object);
          sao.set("sentence", sent);
          sao.set("remark", "PHRASE");
          list.add(sao);
        }
      }
    }

    return list;
  }
  
  private static String clean(String phrase) {
    phrase = stanford.getLemma(phrase);
    phrase = phrase.replaceAll("-", " ");
    return phrase;
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