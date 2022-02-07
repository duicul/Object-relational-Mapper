package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import criteria.Criteria;
import loader.TableData;

public class SQLiteDBConenctor extends DBConnector {
	private String username, password, database, driver;
	private boolean show_querries;

	public SQLiteDBConenctor(long port, String hostname, String username, String password, String database,
			boolean show_querries) {
		this.username = username;
		this.password = password;
		this.database = database;
		this.show_querries = show_querries;
		this.driver = "org.sqlite.JDBC";
	}

	@Override
	public boolean createTable(TableData td) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + database, username, password);
		return con;
	}

	@Override
	public List<String> generateCreateTableQuery(TableData td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateDeleteTableQuery(TableData td) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> generateCreateQuery(Object o,Class<?> subClas) throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateReadQuery(Criteria c) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateUpdateQuery(Criteria c, Object o) throws IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateDeleteQuery(Criteria c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Criteria createCriteria(TableData current) {
		// TODO Auto-generated method stub
		return null;
	}

}
