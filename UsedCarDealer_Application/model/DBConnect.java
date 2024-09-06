package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	static final String DB_URL = "jdbc:mysql://www.papademas.net:3307/510labs?autoReconnect=true&useSSL=false";
	// Database credentials
	static final String USER = "db510", PASS = "510";

	private static Connection conn;
	static {
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DBConnect() throws SQLException {
	}

	public Connection getConnection() {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return conn;
		}
		return conn;
	}

	public void closeConnection() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}

	public void createUsersTable() {
		String checkQuery = "SELECT * FROM information_schema.tables WHERE table_schema = '510labs' AND table_name = 'userList'";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkQuery)) {

			// If the table does not exist, create it
			if (!rs.next()) {
				String sqlCreate = "CREATE TABLE IF NOT EXISTS userList ("
						+ "username VARCHAR(255) NOT NULL PRIMARY KEY," + "password VARCHAR(255) NOT NULL,"
						+ "role VARCHAR(50) NOT NULL" + ")";
				stmt.execute(sqlCreate);
				String sqlCreateCarModel = "CREATE TABLE IF NOT EXISTS carmodel (" + "carid INT NOT NULL PRIMARY KEY,"
						+ "make VARCHAR(255) NOT NULL," + "model VARCHAR(255) NOT NULL," + "year INT NOT NULL,"
						+ "price DECIMAL(10, 2) NOT NULL," + "color VARCHAR(255) NOT NULL," + "quantity INT NOT NULL"
						+ ")";
				stmt.execute(sqlCreateCarModel);

				// Insert an initial entry
				String sqlInsertAdmin = "INSERT INTO userList (username, password, role) VALUES ('admin', 'adminPass', 'administrator')";
				stmt.execute(sqlInsertAdmin);
				String sqlInsertUser = "INSERT INTO userList (username, password, role) VALUES ('user', 'userPass', 'user')";
				stmt.execute(sqlInsertUser);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
