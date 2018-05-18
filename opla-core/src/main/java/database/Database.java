package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exceptions.MissingConfigurationException;

public class Database {

    private static String pathDatabase;

    private Database() {
    }

    /**
     * Create a connection with database and returns a Statement to working
     * with.
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws com.ufpr.br.opla.exceptions.MissingConfigurationException
     */
    public static Connection getConnection() throws MissingConfigurationException, SQLException, ClassNotFoundException {

	if ("".equals(pathDatabase))
	    throw new MissingConfigurationException("Path to database should not be blank");

	return makeConnection();
    }

    private static Connection makeConnection() throws SQLException, ClassNotFoundException {
	Class.forName("org.sqlite.JDBC");
	return DriverManager.getConnection("jdbc:sqlite:" + pathDatabase);
    }

    public static void setPathToDB(String path) {
	pathDatabase = path;
    }

}
