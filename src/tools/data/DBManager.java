package tools.data;

import item.MakeInstrumentationUtil;
import item.Patent;
import item.SaoTuple;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import tools.nlp.SAOExtractor;

public class DBManager {
	private static DBManager instance;
	private static Properties props;
	private String driver;
	private String url;
	private String user;
	private String password;

	private DBManager() throws FileNotFoundException, IOException {
		loadProperties();
		driver = getConfig("driver");
		url = getConfig("url");
		user = getConfig("user");
		password = getConfig("password");
	}

	public static DBManager getInstance() throws FileNotFoundException,
	    IOException {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	private static void loadProperties() throws FileNotFoundException,
	    IOException {
		props = new Properties();
		props.load(new FileInputStream("config.properties"));
	}

	private static String getConfig(String key) {
		return props.getProperty(key);
	}

	public void open() {
		Base.open(driver, url, user, password);
	}

	public void close() {
		Base.close();
	}

}