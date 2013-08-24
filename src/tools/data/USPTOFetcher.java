package tools.data;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import core.dbmodel.Patent;

public class USPTOFetcher {
  public static int count = 0;
  public static Document result = null;

  public static List<String> fetch(String fileName) throws IOException {
    List<String> list = new ArrayList<String>();

    BufferedReader br = new BufferedReader(new FileReader(fileName));
    String query = br.readLine();
    br.close();

    result = query(query, 1);
    count = count();
    add(list);
    
    int page = (int) (count / 50) + 1;
    if (page > 1) {
      for (int i = 2; i < page + 1; i++) {
        result = query(query, i);
        add(list);
      }
    }
    
    Collections.sort(list);
    return list;
  }

  // remove elements based on tag list
  private static Document removeElements(Document doc, String[] list) {
    for (int i = 0; i < list.length; i++)
      doc.getElementsByTag(list[i]).remove();
    return doc;
  }

  private static void add(List<String> list) {
    Element table = result.getElementsByTag("table").get(1);
    Elements tr = table.getElementsByTag("tr");
    tr.remove(0);
    Iterator<Element> i = tr.iterator();
    while (i.hasNext()) {
      Element e = i.next();
      String id = e.getElementsByTag("td").get(1).text();
      id = id.replaceAll(",", "");
      list.add(id);
    }
  }
  
  // count relative patents
  private static int count() {
    Document doc = result.clone();
    String[] tag_list = { "head", "center", "form", "table", "p", "b", "i" };
    doc = removeElements(doc, tag_list);
    String s = doc.text();
    return Integer.parseInt(s = s.substring(2, s.indexOf("patents") - 1));
  }

  private static Document query(String query, int page) throws IOException {
    // replace all char for URL conversion
    query = query.replaceAll(" ", "%20");
    String url = "http://patft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&u=%2Fnetahtml%2FPTO%2Fsearch-adv.htm&r=0&p=" + page + "&f=S&l=50&Query=" + query + "&d=PTXT";
    System.out.println(url);
    return Jsoup.connect(url).timeout(0).get();
  }
}
