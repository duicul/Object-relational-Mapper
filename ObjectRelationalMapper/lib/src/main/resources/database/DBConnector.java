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
import loader.ForeignTable;
import loader.TableData;

public abstract class DBConnector {

	/**
	 * Create table , from subclass to superclass
	 * 
	 * @param o
	 * @return
	 */
	public boolean createTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> sql = this.generateCreateTableQuery(current, null);
			for (int i = sql.size() - 1; i >= 0; i--) {
				System.out.println("Execute "+sql.get(i));
				stmt.addBatch(sql.get(i));}
			stmt.executeBatch();
			con.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Create / Insert data , from superclass to subclass
	 * 
	 * @param o
	 * @return
	 */
	public boolean deleteTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> sql = this.generateDeleteTableQuery(current);
			for (String query : sql)
				stmt.addBatch(query);
			stmt.executeBatch();
			;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Create / Insert data , from subclass to superclass
	 * 
	 * @param o
	 * @return
	 */
	public boolean create(Object o) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> sql = this.generateCreateQuery(o, o.getClass(), null);
			for (int i = sql.size() - 1; i >= 0; i--)
				stmt.addBatch(sql.get(i));
			stmt.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Read from joined tables based on a criteria (instantiates the objects)
	 * 
	 * @param c
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
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
				for (TableData curTable = current; curTable != null; curTable = curTable.parentTable) {
					for (int i = 1; i <= no_col; i++) {
						String col_name = rsmd.getColumnName(i);

						if (curTable.pk != null && curTable.pk.autoincrement()
								&& curTable.pk.name().contentEquals(col_name)) {
							Object obj = rs.getObject(i);
							curTable.pk_field.set(ret, obj);
							continue;
						}
						for (ColumnData cd : curTable.lcd) {
							if (cd.col != null && col_name.equals(cd.col.name())) {
								Object obj = rs.getObject(i);
								Class<?> fieldClass = cd.f.getType();
								cd.f.set(ret, this.changeTypes(obj, fieldClass));

								break;
							}
							if (curTable.pk != null && col_name.equals(curTable.pk.name())) {
								Object obj = rs.getObject(i);
								curTable.pk_field.set(ret, obj);
								break;
							}
						}
					}

					for (TableData foreignTab : curTable.associatedTables.keySet()) {
						Criteria clonedCrit = c.adaptForTable(foreignTab);
						clonedCrit.addForeignTableCriteria(curTable, ret);
						List<Object> foreignObj = this.read(clonedCrit);
						ForeignTable ft = curTable.associatedTables.get(foreignTab);
						if (ft != null && ft.oto != null) {
							if (foreignObj.size() == 1)
								ft.f.set(ret, foreignObj.get(0));
						} else if (ft != null && ft.otm != null) {
							ft.f.set(ret, foreignObj);
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

	private Object changeTypes(Object obj, Class<?> castClass) {
		Object changed = obj;
		if (obj instanceof Double) {
			if (castClass == float.class)
				return ((Double) obj).floatValue();
			else if (castClass == double.class)
				return ((Double) obj).doubleValue();
		} else if (obj instanceof Float) {
			if (castClass == float.class)
				return ((Float) obj).floatValue();
		} else {
			if (obj instanceof Integer) {
				if (castClass == int.class)
					return ((Integer) obj).intValue();
			}
		}
		return changed;
	}

	/**
	 * Update values into table based on criteria
	 * 
	 * @param c
	 * @param o
	 * @return
	 */
	public boolean update(Criteria c, Object o) {
		try {

			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> query = this.generateUpdateQuery(c, o);
			if (query.size() > 0) {
				String sql = query.get(0);
				stmt.executeUpdate(sql);
				return true;
			} else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Delete data from joined tables (inheritance) using a criteria
	 * 
	 * @param c
	 * @return
	 */
	public boolean delete(Criteria c) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = this.generateDeleteQuery(c);
			stmt.executeUpdate(sql);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public abstract List<String> generateCreateTableQuery(TableData td, TableData foreignTable);

	public abstract List<String> generateDeleteTableQuery(TableData td);

	public abstract List<String> generateCreateQuery(Object o, Class<?> subClass, Class<?> foreignTable)
			throws IllegalArgumentException, IllegalAccessException;

	public abstract String generateReadQuery(Criteria c) throws ClassNotFoundException, SQLException;

	public abstract List<String> generateUpdateQuery(Criteria c, Object o)
			throws IllegalArgumentException, IllegalAccessException;

	public abstract String generateDeleteQuery(Criteria c);

	public abstract Connection getConnection() throws SQLException, ClassNotFoundException;

	public abstract Criteria createCriteria(TableData current);

	public abstract String getDataBaseType(Type type);
}
