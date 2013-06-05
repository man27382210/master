package tools.citation;

import java.io.IOException;
import java.util.List;

import item.Feature;
import item.Patent;
import item.Patents;

public class ClassFeature {

  public void assign(Patents dataset) throws Exception {
    List<String> list = dataset.getNovelIdList();
    // assign class feature
    for (Patent p : dataset)
      assign(p, list);
  }
  
  public void assign(Patent p, List<String> list) {
    String id = p.getString("patent_id");
    boolean novelty = list.contains(id);
    
    long count = Feature.count("patent_id = ?", id);
    Feature f = null;
    if (count == 0) {
      f = new Feature();
    } else {
      f = Feature.findFirst("patent_id = ?", id);
    }
    f.set("patent_id", id);
    f.set("class", novelty);
    f.saveIt();
  }
}
