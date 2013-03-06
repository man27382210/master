package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;


import ntu.im.bilab.jacky.master.db.DBSource;
import ntu.im.bilab.jacky.master.tools.IssueYearFinder;


public class MainController {

	public static void main(String[] args) {
		try {
			IssueYearFinder iyf = new IssueYearFinder();
			PatentFetcher pf = new PatentFetcher();
			pf.execute();
			List<Patent> patents = pf.getPatentList();
	    DBSource db = new DBSource();
	    Connection conn = db.getConnection();
	    Statement stmt = conn.createStatement();
	    
	    for (Patent p : patents) {
	    	String patent_id = p.getPatentId();
	    	String year = iyf.getIssueYear(patent_id);
	    	p.setYear(year);
	    	String sql = "select abstract from uspto_" + year + " where patent_id = '" + patent_id + "'";
	    	//System.out.println(sql);
	    	ResultSet rs = stmt.executeQuery(sql);
	    	while (rs.next()) {
	    		String abstracts = rs.getString("Abstract");
	    		p.setAbstracts(abstracts);
	    		//System.out.println(claims);
	    	}
	    }
	    
	    db.closeConnection(conn);
	    
	    for (Patent p : patents) {
	    	String abstracts = p.getAbstracts();
	    	List<String> sentences = Arrays.asList(OpenNLP.getSentence(abstracts));
	    	for (String sentence : sentences){
	    		List<SAOTuple> sao_list = SAOExtractor.getSAOTuple(sentence);
	    		if(sao_list != null) p.getSAOList().addAll(sao_list);
	    	}
	    	p.toString();
	    }
	   
	    
	    
    } catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
	}
}
