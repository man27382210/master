package core.model.sao;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import core.dbmodel.Patent;
import core.dbmodel.SAO;

import tools.nlp.TFIDFRanker;

public class TFIDFFilter {
  private static TFIDFFilter instance = null;
  private static Patent patent = null;
  private static TFIDFRanker ranker = null;

  public static TFIDFFilter getInstance() {
    if (instance == null) {
      instance = new TFIDFFilter();
    }
    return instance;
  }

  public static void filter(List<Patent> list, int topK) throws IOException {
    ranker = TFIDFRanker.getInstance();
    ranker.load(list);
    for (Patent p : list)
      filterSAOTupleList(p, topK);
  }

  public static void filterSAOTupleList(Patent patent, int topK) {
    TFIDFFilter.patent = patent;
    List<SAO> list = patent.getSaoTupleList();
    SAOTupleComparator comparator = new SAOTupleComparator();
    Collections.sort(list, comparator);
    for (SAO t : list) {
      System.out.println(ranker.getTFIDF(patent.getString("id"), t) + " TFIDF:" + t.toString());
    }
    if (list.size() > topK)
      list = list.subList(0, topK);
    //System.out.println(list);
    patent.setSaoTupleList(list);

  }

  public static class SAOTupleComparator implements Comparator<Object> {
    @Override
    public int compare(Object arg0, Object arg1) {
      SAO t1 = (SAO) arg0;
      SAO t2 = (SAO) arg1;
      String id = patent.getString("patent_id");

      double v1 = ranker.getTFIDF(id, t1.getString("subject")) + ranker.getTFIDF(id, t1.getString("predicate")) + ranker.getTFIDF(id, t1.getString("object"));

      double v2 = ranker.getTFIDF(id, t2.getString("subject")) + ranker.getTFIDF(id, t2.getString("predicate")) + ranker.getTFIDF(id, t2.getString("object"));

      Double d1 = new Double(v1);
      Double d2 = new Double(v2);

      // System.out.println(d1 + " " + t1.toString());
      // System.out.println(d2 + " " + t2.toString());

      return d2.compareTo(d1);
    }
  }

}
