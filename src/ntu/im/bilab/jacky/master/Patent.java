package ntu.im.bilab.jacky.master;

import java.util.ArrayList;
import java.util.List;

public class Patent {

	String patent_id;
	String year;
	String abstracts;
	String claims;
	List<SAOTuple> sao_list = new ArrayList<SAOTuple>();

	public String getPatentId() {
		return patent_id;
	}

	public void setPatentId(String patent_id) {
		this.patent_id = patent_id;
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

	public List<SAOTuple> getSAOList() {
		return sao_list;
	}

	public void setSAOList(List<SAOTuple> sao_list) {
		this.sao_list = sao_list;
	}

	@Override
	public String toString() {
		return "Patent [patent_id=" + patent_id + ", year=" + year
				+ ", abstracts=" + abstracts + ", claims=" + claims
				+ ", sao_list=" + sao_list + "]";
	}

}
