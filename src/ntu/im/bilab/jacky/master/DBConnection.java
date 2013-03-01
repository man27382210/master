package ntu.im.bilab.jacky.master;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnection {
	private static Properties props;
	
	private static void loadProperties() {
		props = new Properties();
		try {
	    props.load(new FileInputStream("config.properties"));
    } catch (FileNotFoundException e) {
	    e.printStackTrace();
    } catch (IOException e) {
	    e.printStackTrace();
    }
	}
	
	private static String getConfig(String key) {
		return props.getProperty(key);
	}
	
  public static void main(String[] args) {
    loadProperties();

    String driver = getConfig("driver"); 
    String url = getConfig("url");  
    String user = getConfig("user");  
    String password = getConfig("password");

    try { 
        Class.forName(driver); 
        Connection conn = 
           DriverManager.getConnection(url, 
                              user, password);
        for (int i = 1976 ; i<= 2009 ; i++){
        	String sql = "INSERT uspto SELECT * FROM patent_value.uspto_" + i ;
        	Statement stmt = conn.createStatement();
        	stmt.execute(sql);
        }
        
        if(conn != null && !conn.isClosed()) {
            System.out.println("資料庫連線測試成功！"); 
            conn.close();
        }
        
    } 
    catch(ClassNotFoundException e) { 
        System.out.println("找不到驅動程式類別"); 
        e.printStackTrace(); 
    } 
    catch(SQLException e) { 
        e.printStackTrace(); 
    } 
  } 

}
