package item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;

import tools.data.GoogleCrawler;

public class Patent extends Model {
	String id;
	String year;
	String abstracts;
	String claims;
	String description;
	String fullText;
	List<SaoTuple> saoTupleList = new ArrayList<SaoTuple>();

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

	public List<SaoTuple> getSaoTupleList() {
		return saoTupleList;
	}

	public void setSaoTupleList(List<SaoTuple> saoTupleList) {
		this.saoTupleList = saoTupleList;
	}

	@Override
	public String toString() {
		return "Patent [id=" + id + ", saoTupleList=" + saoTupleList + "]";
	}

	public void show() {
		System.out.println("--- Patent info ---");
		System.out.println("id : " + id);
		for (SaoTuple t : saoTupleList) {
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
