package tool.others;

import java.io.FileNotFoundException;
import java.io.IOException;

import core.dbmodel.MakeInstrumentationUtil;

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

			// move from 1976 to 2009
			for (int i = 1976; i <= 2009; i++) {
				long t0 = System.currentTimeMillis();
				long count = m.count("mypaper.content_" + i);
				System.out.println("insert year : " + i + ", count : " + count);
				m.exec("insert into `master`.`uspto2` (`patent_id`, `issued_date`, `issued_year`, `inventors`, `assignee`, `references cited`, `abstract`, `claims`, `description`, `summary`, `title`) select * from mypaper.content_"
						+ i);
				long t1 = System.currentTimeMillis();
				System.out.println("Done in " + (t1 - t0) + " msec.");
			}

			// long t0 = System.currentTimeMillis();
			// m.exec("insert into uspto2 select * from uspto");
			// long t1 = System.currentTimeMillis();
			// System.out.println("Done in " + (t1 - t0) + " msec.");

			// long t0 = System.currentTimeMillis();
			//
			// m.findAll("select * from uspto2 where patent_id = 'RE29091'");
			// long t1 = System.currentTimeMillis();
			// System.out.println("Done in " + (t1 - t0) + " msec.");
			//

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
