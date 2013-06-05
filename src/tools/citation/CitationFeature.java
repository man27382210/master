package tools.citation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tools.data.DBManager;
import tools.model.SAOWN;
import item.Feature;
import item.Patent;
import item.Patents;
import item.StanfordTree;

public class CitationFeature {
  public Map<String, Document> cDocCache = new HashMap<String, Document>();

  public enum DocType {
    Summary, Citations
  }

  public void assign(Patents patents) throws IOException {
    for (Patent p : patents) {
      assign(p, patents);
      System.out.println("=====");
    }
  }

  public void assign(Patent p, Patents patents) throws IOException {
    String id = p.getString("patent_id");
    System.out.println("current id:" + id);

    long count = Feature.count("patent_id = ?", id);
    Feature f = null;
    if (count == 0) {
      f = new Feature();
    } else {
      f = Feature.findFirst("patent_id = ?", id);
    }

    String longId = getLongID(id);
    Document sDoc = getDocument(longId, DocType.Summary);
    
    Document cDoc = null;
    if(cDocCache.containsKey(longId)) {
      cDoc = cDocCache.get(longId);
    } else {
      cDoc = getDocument(longId, DocType.Citations);
      cDocCache.put(longId, cDoc);
    }
    
    int bc = getBC(cDoc);
    int sl = getSL(cDoc);
    double orig = getORIG(sDoc, cDoc, bc);
    double simbcs = getSIMBCS(cDoc, p, patents);

    f.set("patent_id", id);
    f.set("backward_citation", bc);
    f.set("science_linkage", sl);
    f.set("uspc_originality", orig);
    f.set("sim_bc_structure", simbcs);
    f.saveIt();

  }

  public double getSIMBCS(Document cDoc, Patent current, Patents patents) throws IOException {
    String id = current.getString("patent_id");
    Set<String> bcIdList = getBCIds(cDoc);

    double sum = 0.0;
    int count = 0;
    for (Patent prior : patents) {
      String priorId = prior.getString("patent_id");
      if (id.compareTo(priorId) > 0) {
        count++;
        System.out.println(priorId);
        String longId = getLongID(priorId);
        
        Document priorCDoc = null;
        if(cDocCache.containsKey(longId)) {
          priorCDoc = cDocCache.get(longId);
        } else {
          priorCDoc = getDocument(longId, DocType.Citations);
          cDocCache.put(longId, priorCDoc);
        }
        
        Set<String> priorBCIdList = getBCIds(priorCDoc);
        double jacard = getJacard(bcIdList, priorBCIdList);
        sum += jacard;
      }
    }

    double similarity_backward_citation_structure = 0.0;
    if (count != 0) {
      similarity_backward_citation_structure = sum / count;
    }
    System.out.println("simbcs:" + similarity_backward_citation_structure);
    return similarity_backward_citation_structure;
  }

  public double getJacard(Set<String> bcIdList, Set<String> priorBCIdList) {
    Set<String> total = new HashSet<String>();
    total.addAll(bcIdList);
    total.addAll(priorBCIdList);

    int union = total.size();
    int intersection = bcIdList.size() + priorBCIdList.size() - union;

    return (double) intersection / (double) union;
  }

  // backward citation id
  public Set<String> getBCIds(Document cdoc) {
    Set<String> bcIdList = new HashSet<String>();
    ListIterator<Element> itr = cdoc.select("div[id=outContent] ul[class=bulletList] li a").listIterator();
    while (itr.hasNext()) {
      String id = itr.next().text();
      bcIdList.add(id);
    }
    return bcIdList;
  }

  // science linkages
  public int getSL(Document doc) {
    String text = doc.select("div[id=citations] li[id=publications]").text();
    String[] tmp = text.split(" ");
    int publications = Integer.valueOf(tmp[0]);
    System.out.println("sl:" + publications);
    return publications;
  }

  // backward citation count
  public int getBC(Document doc) {
    String text = doc.select("div[id=citations] li[id=out]").text();
    String[] tmp = text.split(" ");
    int backword_citation = Integer.valueOf(tmp[0]);
    System.out.println("bc:" + backword_citation);
    return backword_citation;
  }

  // similarity of backward citation structure
  public double getBCS(Patent p) {
    return 0.0;
  }

  // USPC originality
  public double getORIG(Document sdoc, Document cdoc, int bc) throws IOException {
    // get count of backward citation
    if (bc == 0)
      return 0.0;

    // get main USPC from this document
    String mainUSPC = getMainUSPC(sdoc);

    // get backward citations' summary doc
    List<Document> bcDocList = getBCDoc(cdoc);

    // get backward citations' main USPC
    int sameUSPC = 0;
    for (Document doc : bcDocList) {
      String bcMainUSPC = getMainUSPC(doc);
      // System.out.println(bcMainUSPC);
      if (mainUSPC.equals(bcMainUSPC))
        sameUSPC++;
    }
    double tmp = (double) sameUSPC / (double) bc;
    double originality = 1 - tmp * tmp;
    System.out.println("orig:" + originality);
    return originality;
  }

  public String getMainUSPC(Document sdoc) {
    String mainUSPC = null;
    try {
      mainUSPC = sdoc.select("div[id=patentSummaryLong] ul[class=filterLink inlineFloat] li a[data-filter_param=classNat]").first().text();
    } catch (NullPointerException e) {
      return "";
    }
    return mainUSPC;
  }

  public List<Document> getBCDoc(Document cdoc) throws IOException {
    List<Document> bcDocList = new ArrayList<Document>();
    ListIterator<Element> itr = cdoc.select("div[id=outContent] ul[class=bulletList] li a").listIterator();
    while (itr.hasNext()) {
      String id = itr.next().text();
      bcDocList.add(getDocument(id, DocType.Summary));
    }
    return bcDocList;
  }

  public String getLongID(String id) throws IOException {
    // convert "4241621" to "US_4241621_A"
    String url = "http://www.lens.org/lens/search?q=" + id;
    Document doc = Jsoup.connect(url).timeout(0).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52").get();
    String longID = doc.select("tr[id*=" + id + "]").attr("id");
    return longID;
  }

  public Document getDocument(String id, DocType type) throws IOException {
    String url = null;
    Document doc = null;
    // fetch US_4241621_A page
    if (type == DocType.Summary) {
      url = "http://www.lens.org/lens/patent/" + id;
      doc = Jsoup.connect(url).timeout(0).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52").get();
    } else if (type == DocType.Citations) {
      url = "http://www.lens.org/lens/patent/" + id + "/citations";
      doc = Jsoup.connect(url).timeout(0).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52").get();
    }
    return doc;
  }

  public static void main(String[] args) throws Exception {
    DBManager mgr = DBManager.getInstance();
    mgr.open();
    Patents dataset = new Patents("test", "data/dataset-test.txt", "data/dataset-test.txt");

    CitationFeature f = new CitationFeature();
    f.assign(dataset);

    mgr.close();
  }

}
