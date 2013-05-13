package depreciate;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.WS4J;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class Demo {

  private static ILexicalDatabase db = new NictWordNet();
  private static RelatednessCalculator[] rcs = { new WuPalmer(db) };

  private static void run(String word1, String word2) {
    
    for (RelatednessCalculator rc : rcs) {
      double s = rc.calcRelatednessOfWords(word1, word2);
      System.out.println(rc.getClass().getName() + "\t" + s);
    }
  }

  public static void main(String[] args) {
    WS4JConfiguration.getInstance().setCache(true);
    long t2 = System.currentTimeMillis();
    double d = WS4J.runWUP("act", "moderate");
    long t3 = System.currentTimeMillis();
    System.out.println(d + " Done in " + (t3 - t2) + " msec.");
    
    long t0 = System.currentTimeMillis();
    run("act", "moderate");
    long t1 = System.currentTimeMillis();
    System.out.println("Done in " + (t1 - t0) + " msec.");
    
   
    
    
  }
}
