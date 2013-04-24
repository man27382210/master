package util;

import item.SAO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import tools.data.DBManager;

public class Test {
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
			
			List<SAO> list = SAO.findAll();
			for (SAO t : list) {
				System.out.println(t.toString());
			}
			
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
