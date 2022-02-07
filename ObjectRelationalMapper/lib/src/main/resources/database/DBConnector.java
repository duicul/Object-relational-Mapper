package database;

import criteria.Criteria;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import loader.ClassMapper;
import loader.ColumnData;
import loader.TableData;

public abstract class DBConnector {
	
	public boolean createTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> sql = this.generateCreateTableQuery(current);
			for(int i=sql.size()-1;i>=0;i--)
				stmt.addBatch(sql.get(i));
			stmt.executeBatch();
			con.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean deleteTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = this.generateDeleteTableQuery(current);
			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean create(Object o) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> sql = this.generateCreateQuery(o,o.getClass());
			for(int i=sql.size()-1;i>=0;i--)
				stmt.addBatch(sql.get(i));
			stmt.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Object> read(Criteria c) throws ClassNotFoundException, SQLException {
		TableData current = c.td;
		List<Object> lo = new LinkedList<Object>();
		Connection con = this.getConnection();
		Statement stmt = con.createStatement();
		String sql = this.generateReadQuery(c);
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int no_col = rsmd.getColumnCount();
		try {
			Constructor<?> cons = current.class_name.getConstructor();
			Object ret;

			while (rs.next()) {
				ret = cons.newInstance();
				for (int i = 1; i <= no_col; i++) {
					String col_name = rsmd.getColumnName(i);
					for (TableData curTable = current; curTable != null; curTable = curTable.parentTable) {

						if (curTable.pk != null && curTable.pk.autoincrement()
								&& curTable.pk.name().contentEquals(col_name)) {
							Object obj = rs.getObject(i);
							curTable.pk_field.set(ret, obj);
							continue;
						}
						for (ColumnData cd : curTable.lcd) {
							if (cd.col != null && col_name.equals(cd.col.name())) {
								Object obj = rs.getObject(i);
								cd.f.set(ret, obj);
								break;
							}
							if (curTable.pk != null && col_name.equals(curTable.pk.name())) {
								Object obj = rs.getObject(i);
								curTable.pk_field.set(ret, obj);
								break;
							}
						}
					}
				}
				lo.add(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lo;
	}

	public boolean update(Criteria c, Object o) {
		try {
			
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = this.generateUpdateQuery(c, o);
			stmt.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Criteria c) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = this.generateDeleteQuery(c);
			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery(sql);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public abstract List<String> generateCreateTableQuery(TableData td);
	public abstract String generateDeleteTableQuery(TableData td);
	public abstract List<String> generateCreateQuery(Object o,Class<?> subClass)  throws IllegalArgumentException, IllegalAccessException;
	public abstract String generateReadQuery(Criteria c) throws ClassNotFoundException, SQLException;
	public abstract String generateUpdateQuery(Criteria c,Object o) throws IllegalArgumentException, IllegalAccessException ;
	public abstract String generateDeleteQuery(Criteria c);
	
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
