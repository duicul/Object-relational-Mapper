package database;

import criteria.Criteria;
import exception.ForeignKeyReplacementError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import loader.ClassMapper;
import loader.ColumnData;
import loader.ForeignTable;
import loader.TableData;

public abstract class DBConnector {
	public static final String PARENT_FOREIGN_KEY = "PARENT_KEY";
	public static final String FOREIGN_KEY = "FOREIGN_KEY";

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
				System.out.println("Execute " + sql.get(i));
				stmt.addBatch(sql.get(i));
			}
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
	public boolean create(Object o, Class<?> foreignTable, Object assocKey) {
		try {
			Connection con = this.getConnection();
			// Statement stmt = con.createStatement();

			TableData td = ClassMapper.getInstance().getTableData(o.getClass());
			List<TableData> tables = new LinkedList<TableData>();
			Object generatedKey = null;
			if (td.parentTable != null) {
				for (TableData parent = td.parentTable,
						child = td; child != null; child = parent, parent = parent == null ? null : parent.parentTable)
					tables.add(child);
			} else
				tables.add(td);
			Collections.reverse(tables);
			for (TableData current : tables) {
				// Object parentObj = current.class_name.cast(o);
				boolean isAssoc = false;
				if (foreignTable != null) {
					TableData tdAssoc = ClassMapper.getInstance().getTableData(foreignTable);
					for (TableData t : tdAssoc.associatedTables.keySet()) {
						if (t.table.name().equals(current.table.name()))
							isAssoc = true;
					}
				}
				String sql = "";
				if (isAssoc)
					sql = this.generateCreateQuery(o, current.class_name, foreignTable);
				else
					sql = this.generateCreateQuery(o, current.class_name, null);
				String keys[] = { current.pk.name() };
				if (generatedKey != null) {
					if (sql.contains(PARENT_FOREIGN_KEY)) {
						sql = sql.replace(PARENT_FOREIGN_KEY, generatedKey.toString());
					} else
						throw new ForeignKeyReplacementError(sql);
				}

				if (assocKey != null && isAssoc) {
					if (sql.contains(FOREIGN_KEY)) {
						sql = sql.replace(FOREIGN_KEY, assocKey.toString());
					} else
						throw new ForeignKeyReplacementError(sql);
				}

				PreparedStatement ps = con.prepareStatement(sql, keys);
				System.out.println(sql);
				ps.executeUpdate();

				ResultSet rs = ps.getGeneratedKeys();

				if (rs.next()) {
					generatedKey = rs.getObject(1);
				}

				for (TableData assoctd : current.associatedTables.keySet()) {
					Object foreObj = current.associatedTables.get(assoctd).f.get(o);
					if (foreObj != null) {
						if (current.associatedTables.get(assoctd).oto != null)
							this.create(foreObj, current.class_name, generatedKey);
						else if (current.associatedTables.get(assoctd).otm != null) {
							List<Object> lotm = (List<Object>) foreObj;
							for (Object ob : lotm) {
								this.create(ob, current.class_name, generatedKey);
							}
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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

	public abstract String generateCreateQuery(Object o, Class<?> subClass, Class<?> foreignTable)
			throws IllegalArgumentException, IllegalAccessException;

	public abstract String generateReadQuery(Criteria c) throws ClassNotFoundException, SQLException;

	public abstract List<String> generateUpdateQuery(Criteria c, Object o)
			throws IllegalArgumentException, IllegalAccessException;

	public abstract String generateDeleteQuery(Criteria c);

	public abstract Connection getConnection() throws SQLException, ClassNotFoundException;

	public abstract Criteria createCriteria(TableData current);

	public abstract String getDataBaseType(Type type);
}
