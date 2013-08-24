package core.similarity;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import core.dbmodel.Patent;
import core.dbmodel.SAO;


public interface Similarity {

  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException;

}
