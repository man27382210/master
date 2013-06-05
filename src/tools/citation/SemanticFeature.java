package tools.citation;

import java.io.IOException;
import java.util.List;

import item.Feature;
import item.Patent;
import item.Patents;
import tools.data.DBManager;
import tools.measure.MoehrleNovelty;
import tools.model.LSA;
import tools.model.Lucene;
import tools.model.PLSA;
import tools.model.SAOWN;
import tools.model.VSM;
import tools.model.Lucene.WeightType;
import tools.sim.PatentMatrixGenerator;
import util.MakeInstrumentationUtil;

public class SemanticFeature {

  public void assign(Patents dataset) throws Exception {
    // assign sao method
    SAOWN saown = new SAOWN(dataset, 5);
    saown.run();
    for (Patent p : dataset)
      p.putAttribute("novelty_sao", MoehrleNovelty.getNovelty(p));

    // assign vsm method
    VSM vsm = new VSM(new Lucene(dataset, WeightType.TFIDF));
    PatentMatrixGenerator.setSimilarity(vsm);
    PatentMatrixGenerator.generate(dataset);
    for (Patent p : dataset)
      p.putAttribute("novelty_vsm", MoehrleNovelty.getNovelty(p));

    // assign lsa method
    LSA lsa = new LSA(new Lucene(dataset, WeightType.TFIDF));
    lsa.doSVD(32);
    PatentMatrixGenerator.setSimilarity(lsa);
    PatentMatrixGenerator.generate(dataset);
    for (Patent p : dataset)
      p.putAttribute("novelty_lsa", MoehrleNovelty.getNovelty(p));

    // assign plsa method
    PLSA plsa = new PLSA(new Lucene(dataset, WeightType.TF));
    plsa.doPLSA(32, 60, 10);
    PatentMatrixGenerator.setSimilarity(plsa);
    PatentMatrixGenerator.generate(dataset);
    for (Patent p : dataset)
      p.putAttribute("novelty_plsa", MoehrleNovelty.getNovelty(p));

    // assign all feature
    for (Patent p : dataset)
      assign(p);
  }

  public void assign(Patent p) {
    String id = p.getString("patent_id");
    long count = Feature.count("patent_id = ?", id);
    Feature f = null;
    if (count == 0) {
      f = new Feature();
      f.set("patent_id", id);
      f.set("novelty_sao", p.getAttribute("novelty_sao"));
      f.set("novelty_vsm", p.getAttribute("novelty_vsm"));
      f.set("novelty_lsa", p.getAttribute("novelty_lsa"));
      f.set("novelty_plsa", p.getAttribute("novelty_plsa"));
      f.insert();
    
    } else {
      f = Feature.findFirst("patent_id = ?", id);
      f.set("patent_id", id);
      f.set("novelty_sao", p.getAttribute("novelty_sao"));
      f.set("novelty_vsm", p.getAttribute("novelty_vsm"));
      f.set("novelty_lsa", p.getAttribute("novelty_lsa"));
      f.set("novelty_plsa", p.getAttribute("novelty_plsa"));
      f.saveIt();
    }
    
  }

  public static void main(String[] args) throws Exception {
    MakeInstrumentationUtil.make();
    DBManager mgr = DBManager.getInstance();

    mgr.open();
    
    Patents dataset = new Patents("test", "data/dataset-7a.txt", "data/dataset-7a-answer.txt");
    
    SemanticFeature sf = new SemanticFeature();
    sf.assign(dataset);

    CitationFeature cf = new CitationFeature();
    cf.assign(dataset);

    ClassFeature f = new ClassFeature();
    f.assign(dataset);

    mgr.close();
  }

}
