package depreciate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tools.data.DataSetLoader;
import tools.data.PatentFetcher;
import tools.data.USPTOFetcher;

public class Test {

  public static void main(String[] args) throws IOException {
    List<String> list = DataSetLoader.loadID("data/dataset-3a-answer.txt");
    Collections.sort(list);
    System.out.println(list);
    System.out.println(list.size());
    for (String id : list) {
      System.out.println(id); 
    }
  }
  
}
