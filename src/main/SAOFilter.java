package main;

import item.Patent;
import item.SAO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tools.nlp.StopWordRemover;

public class SAOFilter {
  public static StopWordRemover remover = null;

  public static void filter(List<Patent> list) throws IOException {
    remover = new StopWordRemover();
    for (Patent p : list)
      filter(p);
  }

  private static void filter(Patent p) {
    List<SAO> list = p.getSaoTupleList();
    for (int i = 0; i < list.size(); i++) {
      SAO t = list.get(i);
      String subject = t.getString("subject");
      String predicate = t.getString("predicate");
      String object = t.getString("object");
      if (remover.matchFilter(subject) || remover.matchFilter(predicate) || remover.matchFilter(object)) {
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
      BufferedReader br = new BufferedReader(new FileReader("doc/stopword.txt"));
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

    private boolean isStopWord(String word) {
      return stopWordList.contains(word);
    }

    private boolean isAlphabetStr(String word) {
      return word.matches(".*[a-zA-Z]+.*");
    }

    private boolean isGeneral(String word) {
      return generalWordList.contains(word);
    }

    private boolean isLength(String word) {
      return (word.length() > 50) || (word.length() < 2)  ;
    }

    public boolean matchFilter(String word) {
      return (isStopWord(word) || !isAlphabetStr(word) || isGeneral(word) || isLength(word));
    }

  }

}
