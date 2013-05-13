package tools.sim;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import item.Patent;
import item.SAO;

public interface Similarity {

  public double patentSim(Patent p1, Patent p2) throws ParseException, IOException, InterruptedException;

}
