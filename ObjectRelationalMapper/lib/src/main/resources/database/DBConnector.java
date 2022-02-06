package database;

import criteria.Criteria;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import loader.TableData;

public abstract class DBConnector {
	public abstract boolean createTable(TableData td);
	public abstract boolean deleteTable(String tableName);
	public abstract boolean create();
	public abstract List<Object> read(TableData current, Criteria c) throws ClassNotFoundException, SQLException;
	public abstract boolean update();
	public abstract boolean delete();
	
	public abstract Connection getConnection() throws SQLException, ClassNotFoundException;
	
	public abstract Criteria createCriteria(TableData current);
	
	public static String getDataBaseType(Type type) {
		if(type.equals(int.class)||type.equals(Integer.class))
			return "INTEGER";
		if(type.equals(float.class)||type.equals(Float.class))
			return "FLOAT";
		if(type.equals(double.class)||type.equals(Double.class))
			return "DOUBLE";
		if(type.equals(String.class))
			return "VARCHAR(255)";
		if(type.equals(char.class))
			return "VARCHAR(1)";
		return null;
	}
}
