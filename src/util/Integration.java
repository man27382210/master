package util;


import java.io.FileNotFoundException;
import java.io.IOException;

import tools.data.DBManager;

public class Integration {

	public static void main(String[] args) {
		try {
			MakeInstrumentationUtil.make();
			DBManager m = DBManager.getInstance();
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://140.112.107.209:3306/master";
			String user = "root";
			String password = "jacky";
			m.setConfig(driver, url, user, password);
			m.open();
			
//			// move from 1976 to 2008 
//			for (int i = 1977; i <= 2009; i++) {
//				long t0 = System.currentTimeMillis();
//				long count = m.count("mypaper.content_" + i);
//				System.out.println("insert year : " + i + ", count : " + count);
//				m.exec("insert into master.uspto select * from mypaper.content_"
//						+ i);
//				m.exec("insert into master.uspto2 select * from mypaper.content_"
//						+ i);
//				long t1 = System.currentTimeMillis();
//				System.out.println("Done in " + (t1 - t0) + " msec.");
//			}
			
			long t0 = System.currentTimeMillis();
			
			m.findAll("select * from uspto2 where patent_id = 'RE29091'");
			long t1 = System.currentTimeMillis();
			System.out.println("Done in " + (t1 - t0) + " msec.");
			
			
			m.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void moveDataBase() {

	}

}
