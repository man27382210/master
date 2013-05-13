package tools.data;

import item.Patent;
import item.SAO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSetLoader {

  public static List<String> loadID(String fileName) throws IOException {
    List<String> list = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    String line = null;
    while ((line = br.readLine()) != null) {
      list.add(line);
    }
    br.close();
    return list;
  }

  public static List<String> loadID(List<Patent> patentList) throws IOException {
    List<String> list = new ArrayList<String>();
    for (Patent p : patentList)
      list.add(p.getString("patent_id"));
    return list;
  }

  public static List<Patent> loadPatent(List<String> idList) throws Exception {
    List<Patent> list = new ArrayList<Patent>();

    for (String id : idList) {
      Patent p = Patent.findFirst("patent_id = ?", id);
      if (p == null)
        throw new Exception("no match id in database");
      list.add(p);
    }

    return list;
  }

  public static List<Patent> loadSAO(List<Patent> patentList, String type) throws Exception {
    List<Patent> list = new ArrayList<Patent>();

    for (Patent p : patentList) {
      String id = p.getString("patent_id");
      List<SAO> saoList = SAO.where("patent_id = 'US" + id + "' and remark = '" + type + "'");
      if (saoList == null)
        throw new Exception("no sao in database");
      p.setSaoTupleList(saoList);
    }

    return list;
  }

}
