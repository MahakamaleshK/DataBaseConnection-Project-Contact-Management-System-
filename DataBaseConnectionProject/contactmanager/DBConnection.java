package contactmanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DBConnection {
	
	private static final Logger logger = LogManager.getLogger(DBConnection.class);

    private static DBConnection instance;
    private  Connection connection;
    
    private DBConnection() {
    	try {
    		Properties prop = new Properties();
    		prop.load(new FileInputStream("DataBaseConnectionProject/config/dg.config"));
    		String URL = prop.getProperty("dg.url");
    		String USER = prop.getProperty("dg.user");
    		String PASSWORD = prop.getProperty("dg.password");
    		Class.forName("com.mysql.cj.jdbc.Driver");
    		connection = DriverManager.getConnection(URL, USER, PASSWORD);
    		logger.info("Database Connection created...");
    	} catch(SQLException | IOException e){
    		logger.error("Failed to connect to DataBase",e);
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    public static DBConnection getInstance() {
    	if(instance == null) {
    		instance = new DBConnection();
    	}
    	return instance;
    }
    public Connection getConnection() {
    	return connection;
    }
    public void closeConnection() {
    	try {
	    	if(connection != null && !connection.isClosed()) {
	    		connection.close();
	    		logger.info("Database Connection closed!");
	    	}
    	} catch (SQLException e) {
    		logger.error("Error in closing DB connection",e);
    	}
    }
}
