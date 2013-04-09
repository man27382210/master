package item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;

import tools.data.GoogleCrawler;

@IdName("patent_id")
public class Patent extends Model {
	List<SaoTuple> saoTupleList = new ArrayList<SaoTuple>();

	public List<SaoTuple> getSaoTupleList() {
		return saoTupleList;
	}

	public void setSaoTupleList(List<SaoTuple> saoTupleList) {
		this.saoTupleList = saoTupleList;
	}

}
