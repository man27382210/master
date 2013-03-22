package ntu.im.bilab.jacky.master.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ntu.im.bilab.jacky.master.tools.data.GoogleCrawler;

public class Patent {

	String id;
	String year;
	String abstracts;
	String claims;
	String description;
	String fullText;
	List<SAOTuple> saoTupleList = new ArrayList<SAOTuple>();

	public static Patent getPatent(String id) throws IOException {
		GoogleCrawler gc = GoogleCrawler.getInstance();
		return gc.crawl(id);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClaims() {
		return claims;
	}

	public void setClaims(String claims) {
		this.claims = claims;
	}

	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SAOTuple> getSaoTupleList() {
		return saoTupleList;
	}

	public void setSaoTupleList(List<SAOTuple> saoTupleList) {
		this.saoTupleList = saoTupleList;
	}

	@Override
  public String toString() {
	  return "Patent [id=" + id + ", saoTupleList=" + saoTupleList + "]";
  }

	public void show() {
	  System.out.println("--- Patent info ---");
	  System.out.println("id : " + id);
	  for (SAOTuple t : saoTupleList) {
	  	System.out.println(t);
	  }
  }

	public void setFullText(String fullText) {
	  this.fullText = fullText;
  }

	public String getFullText() {
	  return fullText;
  }
	
}
