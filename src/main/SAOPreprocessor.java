package main;

import item.Patent;
import item.StanfordTree;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class SAOPreprocessor {

	public static void saveTree(List<Patent> patentList) {
	  for(Patent p : patentList) 
	  	saveTree(p);
  }

	private static void saveTree(Patent p) {
	  String abs = p.getString("abstract");
	  String clm = p.getString("claims");
	  String des = p.getString("description");
	  
	  StanfordTree s = new StanfordTree();
	  s.set("patent_id", p.getString("patent_id"));

	  
	  
  }
	

	
	

}
