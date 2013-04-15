package util;

import item.MakeInstrumentationUtil;

import java.io.FileNotFoundException;
import java.io.IOException;

import tools.data.DBManager;

public class Integration {

	public static void main(String[] args) {
		try {
			MakeInstrumentationUtil.make();
			DBManager m = DBManager.getInstance();
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://140.112.107.1:3306/jacky";
			String user = "jacky";
			String password = "qaz";
			m.setConfig(driver, url, user, password);
			m.open();
			long count = m.count("patentproject2012.patent_1976");
			System.out.println(count);
			m.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
