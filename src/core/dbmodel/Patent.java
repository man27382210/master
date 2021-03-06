package core.dbmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import tools.data.GoogleCrawler;

@Table("uspto")
@IdName("patent_id")
public class Patent extends Model {
	Map<String, Object> map = new HashMap<String, Object>();
	
	public void putAttribute(String key, Object value) {
		map.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return map.get(key);
	}
	
	public void setSaoTupleList(List<SAO> list) {
		putAttribute("SAO" , list);
	}
	
	public List<SAO> getSaoTupleList() {
		return (List<SAO>) getAttribute("SAO");
	}

	public void setDissimMap(Map<Patent, Double> map) {
		putAttribute("DISSIM" , map);
	}
	
	public Map<Patent, Double> getDissimMap() {
		return (Map<Patent, Double>) getAttribute("DISSIM");
	}
	
	public void showSAO() {
	  List<SAO> list = getSaoTupleList();
	  for (SAO sao : list) {
	    System.out.println(sao.toString());
	  }
	}
}
