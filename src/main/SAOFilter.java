package main;

import item.Patent;
import item.SAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tools.nlp.StanfordUtil;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.util.WordNetUtil;

public class SAOFilter {
  public static StopWordRemover remover = null;
  public static StanfordUtil stanford = StanfordUtil.getInstance();  
  
  public static void filter(List<Patent> list) throws IOException {
    remover = new StopWordRemover();
    for (Patent p : list) {
      System.out.println(p.getString("patent_id"));
      filter(p);
    }

  }

  private static void filter(Patent p) {
    List<SAO> list = p.getSaoTupleList();
    for (int i = 0; i < list.size(); i++) {
      SAO t = list.get(i);
      String subject = t.getString("subject");
      String predicate = t.getString("predicate");
      String object = t.getString("object");

      if (remover.isBadPhrase(subject) || remover.isBadPhrase(predicate) || remover.isBadPhrase(object)) {
        list.remove(i);
        System.out.println("remove " + t.toString());
      } else {
        System.out.println("keep " + t.toString());
      }
    }
    p.setSaoTupleList(list);
  }

  public static class StopWordRemover {
    private List<String> stopWordList = new ArrayList<String>();
    private List<String> generalWordList = new ArrayList<String>();

    public StopWordRemover() throws IOException {
      loadStopWord();
      loadGeneralWord();
    }

    private void loadStopWord() throws IOException {
      BufferedReader br = new BufferedReader(new FileReader("doc/sw.txt"));
      String line = null;
      while ((line = br.readLine()) != null) {
        stopWordList.add(line);
      }
      br.close();
    }

    private void loadGeneralWord() throws IOException {
      BufferedReader br = new BufferedReader(new FileReader("doc/generalword.txt"));
      String line = null;
      while ((line = br.readLine()) != null) {
        generalWordList.add(line);
      }
      br.close();
    }

    private boolean inWordNet(String word) {
      int size = WordNetUtil.wordToSynsets(word, POS.n).size() + WordNetUtil.wordToSynsets(word, POS.v).size();
      if (size == 0)
        return false;
      else
        return true;
    }

    private boolean isStopWord(String word) {
      return stopWordList.contains(word);
    }

    private boolean isAlphabetStr(String word) {
      return word.matches("^[a-zA-Z\\s-]+$");
    }

    private boolean isGeneral(String word) {
      return generalWordList.contains(word);
    }

    public boolean isBadPhrase(String phrase) {
      String[] words = phrase.split(" ");
      for(String word : words) { 
        if (isBadWord(word)) return true;
      }
      return false;
    }
    
    public boolean isBadWord(String word) {
      return (!isAlphabetStr(word) || !inWordNet(word));
    }

    public static void main(String[] args) throws IOException {
      StopWordRemover r = new StopWordRemover();
      System.out.println(r.isAlphabetStr("sub-trail"));
      System.out.println(r.inWordNet("have"));
      System.out.println(stanford.getLemma("has"));
    }
    
  }

}
