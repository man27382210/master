package ntu.im.bilab.jacky.master;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


import ntu.im.bilab.jacky.master.db.DBSource;
import ntu.im.bilab.jacky.master.tools.IssueYearFinder;


public class MainController {

	public static void main(String[] args) {
		try {
			IssueYearFinder iyf = new IssueYearFinder();
			PatentFetcher pf = new PatentFetcher();
			pf.execute();
			List<String> patent_id_list = pf.getPatentIdList();
	    DBSource db = new DBSource();
	    Connection conn = db.getConnection();
	    Statement stmt = conn.createStatement();
	    
	    for (String patent_id : patent_id_list) {
	    	String year = iyf.getIssueYear(patent_id);
	    	String sql = "select claims from uspto_" + year + " where patent_id = '" + patent_id + "'";
	    	stmt.executeQuery(sql);
	    }
	    
	    db.closeConnection(conn);
	    
	    
	    
	    
	    
	    
	    
	    
	    
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
