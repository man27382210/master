package core.similarity;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import core.dbmodel.Patent;
import core.dbmodel.SAO;


public interface SAOBasedSimilarity extends Similarity{

  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException;

  public double saoSim(SAO t1, SAO t2) throws ParseException, IOException, InterruptedException;

  public double pharseSim(String pharse1, String pharse2) throws ParseException, IOException, InterruptedException;

  public double wordSim(String word1, String word2) throws ParseException, IOException, InterruptedException;

}
