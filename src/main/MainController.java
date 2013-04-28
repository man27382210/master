package main;

import item.Patent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tools.data.DBManager;
import tools.data.DataSetLoader;
import tools.measure.MoehrleNovelty;
import tools.nlp.SAOFilter;
import tools.sim.PatentMatrixGenerator;
import util.MakeInstrumentationUtil;
import weka.core.converters.DatabaseLoader;

public class MainController {

	public static void main(String[] args) {
		try {
			MakeInstrumentationUtil.make();
			DBManager mgr = DBManager.getInstance();
			mgr.open();
			MainController.run();
			mgr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void run() throws Exception {

		// 1. load dataset from file (include a list of patent id)
		List<String> idList = DataSetLoader.loadID("doc/dataset1.txt");
		// System.out.println(idList);

		// 2. get data from db by dataset id
		List<Patent> patentList = DataSetLoader.loadPatent(idList);
		// for (Patent p : patentList)
		// System.out.println(p.getString("patent_id"));

		// 3. generate sao triple (single word) for patent (if it have)
		SAOPreprocessor.parseTree(patentList);
		DataSetLoader.loadSAO(patentList, "single");

		// 4. TFIDF ranking for sao and filter sao triple topK
		SAOFilter filter = SAOFilter.getInstance();
		//filter.filter(patentList, 5);

		//5. generate dissimilarity matrix
		PatentMatrixGenerator.generate(patentList);
		// Patent p = patentList.get(0);
		// System.out.println(p.getDissimMap().get(patentList.get(1)));
		MoehrleNovelty.getRanking(patentList);
		
	
	}

}