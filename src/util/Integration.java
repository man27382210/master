package util;

import java.io.FileNotFoundException;
import java.io.IOException;

import tools.data.DBManager;

public class Integration {

	public static void main(String[] args) {
		try {
			DBManager m = DBManager.getInstance();
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://140.112.107.209:3306/master";
			String user = "root";
			String password = "jacky";
			m.setConfig(driver, url, user, password);
			m.open();
			long count = m.count("patents");
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
