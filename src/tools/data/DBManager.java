package tools.data;

import item.Patent;
import item.PatentFullText;
import item.SaoTuple;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.javalite.activejdbc.Base;

import tools.nlp.SAOExtractor;
import util.MakeInstrumentationUtil;

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

	public String getUrl() {
		return url;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return password;
	}
	
	private static void loadProperties() throws FileNotFoundException,
	    IOException {
		props = new Properties();
		props.load(new FileInputStream("config.properties"));
	}

	private static String getConfig(String key) {
		return props.getProperty(key);
	}

	public void setConfig(String driver,String url, String user, String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}
	
	public void open() {
		Base.open(driver, url, user, password);
	}

	public void close() {
		Base.close();
	}
	
	public int exec(String query, Object params){
		return Base.exec(query, params);
	}

	public int exec(String query){
		return Base.exec(query);
	}
	
	public long count(String table, String query, Object params){
		return Base.count(table, query, params);
	}
	
	public long count(String table){
		return Base.count(table);
	}
	
	public List<Map> findAll(String query){
		return Base.findAll(query);
	}
	
}
