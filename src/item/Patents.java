package item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import tools.data.DataSetLoader;

public class Patents extends ArrayList<Patent> {
  private static final long serialVersionUID = 1L;
  private String name;
  private List<String> totalIdList = new ArrayList<String>();
  private List<String> novelIdList = new ArrayList<String>();

  public Patents(String name, String totalIdFileName, String novelIdFileName) throws Exception {
    this.name = name;
    totalIdList = this.loadID(totalIdFileName);
    novelIdList = this.loadID(novelIdFileName);
    this.loadPatent(totalIdList);
  }

  public List<String> loadRank() throws IOException {
    List<String> list = new ArrayList<String>();
    for (Patent p : this)
      list.add(p.getString("patent_id"));
    return list;
  }
  
  private List<String> loadID(String fileName) throws IOException {
    List<String> list = new ArrayList<String>();
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    String line = null;
    while ((line = br.readLine()) != null) {
      list.add(line);
    }
    br.close();
    return list;
  }

  private void loadPatent(List<String> idList) throws Exception {
    for (String id : idList) {
      Patent p = Patent.findFirst("patent_id = ?", id);
      if (p == null)
        throw new Exception("no match id in database");
      this.add(p);
    }
  }
  
  public List<String> getTotalIdList() {
    return totalIdList;
  }

  public void setTotalIdList(List<String> totalIdList) {
    this.totalIdList = totalIdList;
  }

  public List<String> getNovelIdList() {
    return novelIdList;
  }

  public void setNovelIdList(List<String> novelIdList) {
    this.novelIdList = novelIdList;
  }
}
