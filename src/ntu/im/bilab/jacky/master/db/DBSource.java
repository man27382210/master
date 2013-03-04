package ntu.im.bilab.jacky.master.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBSource {
	private static Properties props;
	private String driver;
	private String url;
	private String user;
	private String password;

	public DBSource() throws FileNotFoundException, IOException {
		loadProperties();
		driver = getConfig("driver");
		url = getConfig("url");
		user = getConfig("user");
		password = getConfig("password");
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
	
	private static void loadProperties() throws FileNotFoundException, IOException {
		props = new Properties();
		props.load(new FileInputStream("config.properties"));
	}

	private static String getConfig(String key) {
		return props.getProperty(key);
	}
}
