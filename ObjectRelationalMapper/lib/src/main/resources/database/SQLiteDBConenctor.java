package database;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import criteria.Criteria;
import criteria.MariaDBCriteria;
import criteria.SQLiteCriteria;
import exception.ForeignKeyReplacementError;
import loader.ClassMapper;
import loader.ColumnData;
import loader.TableData;

public class SQLiteDBConenctor extends DBConnector {
	private String dbPath, driver;
	private boolean show_querries;

	public static final String PARENT_FOREIGN_KEY = "PARENT_FOREIGN_KEY";

	public SQLiteDBConenctor(String dbPath, boolean show_querries) {
		this.dbPath = dbPath;
		this.show_querries = show_querries;
		this.driver = "org.sqlite.JDBC";
	}

	@Override
	public List<String> generateCreateTableQuery(TableData td) {
		List<String> batch = new LinkedList<String>();
		String sql = "CREATE TABLE IF NOT EXISTS " + td.table.name();
		sql += "(";
		for (ColumnData cd : td.lcd) {
			String coltype = this.getDataBaseType(cd.f.getGenericType());
			if (coltype != null)
				if (cd.col != null)
					sql += cd.col.name() + " " + coltype + " , ";
			/*
			 * else if(cd.otm!=null) sql+=cd.otm.column()+" "+coltype+" , "; else
			 * if(cd.oto!=null) sql+=cd.oto.column()+" "+coltype+" , ";
			 */
		}
		if (td.pk != null) {
			sql += td.pk.name() + " " + td.pk.type() + " PRIMARY KEY " + (td.pk.autoincrement() ? "AUTOINCREMENT" : "");
		}
		for (TableData foreignTab : td.foreign_val_key) {
			sql += " , " + foreignTab.pk.name() + " " + foreignTab.pk.type();
		}
		if (td.parentTable != null && td.parentTable.pk_field != null) {
			String parentPkType = this.getDataBaseType(td.parentTable.pk_field.getGenericType());
			if (parentPkType != null)
				if (td.parentTable.pk != null) {
					sql += " , " + td.parentTableFK + " " + parentPkType + " , ";
					sql += " CONSTRAINT " + td.table.name() + td.parentTable.table.name();
					sql += " FOREIGN KEY (" + td.parentTableFK + ") REFERENCES " + td.parentTable.table.name() + " ("
							+ td.parentTable.pk.name() + ")";
					sql += " ON DELETE CASCADE ON UPDATE RESTRICT";
				}
		}
		sql += ");";
		if (this.show_querries)
			System.out.println(sql);

		batch.add(sql);
		;
		if (td.parentTable != null) {
			batch.addAll(this.generateCreateTableQuery(td.parentTable));
		}

		return batch;
	}

	@Override
	public List<String> generateDeleteTableQuery(TableData td) {
		List<String> query = new LinkedList<String>();
		String sql = "DROP TABLE " + td.table.name() + " ; ";
		if (this.show_querries)
			System.out.println(sql);
		query.add(sql);
		if (td.parentTable != null) {
			query.addAll(this.generateDeleteTableQuery(td.parentTable));
		}
		return query;
	}

	@Override
	public List<String> generateCreateQuery(Object o, Class<?> subClass)
			throws IllegalArgumentException, IllegalAccessException {
		List<String> query = new LinkedList<String>();
		TableData current = ClassMapper.getInstance().getTableData(subClass);
		String sql = "INSERT INTO " + current.table.name();
		String decl = "", val = "";
		decl += " (";
		val += " (";
		if (current.lcd.size() > 0) {
			if (current.lcd.get(0).f.get(o) instanceof String)
				val += "'" + current.lcd.get(0).f.get(o) + "'";
			else
				val += current.lcd.get(0).f.get(o);
			decl += current.lcd.get(0).col.name();
		}

		for (int colInd = 1; colInd < current.lcd.size(); colInd++) {
			if (current.lcd.get(colInd).f.get(o) instanceof String)
				val += ",'" + current.lcd.get(colInd).f.get(o) + "'";
			else
				val += "," + current.lcd.get(colInd).f.get(o);
			decl += "," + current.lcd.get(colInd).col.name();
		}

		if (current.parentTable != null) {
			if (current.lcd.size() > 0) {
				val += " , ";
				decl += " , ";
			}
			val += PARENT_FOREIGN_KEY;
			decl += current.parentTableFK;
		}

		decl += ")";
		val += ")";
		sql += decl + " VALUES " + val;
		query.add(sql);
		if (this.show_querries)
			System.out.println(sql);
		/*
		 * if (current.parentTable != null) { query.addAll(this.generateCreateQuery(o,
		 * current.parentTable.class_name)); }
		 */
		return query;
	}

	@Override
	public String generateReadQuery(Criteria c) throws ClassNotFoundException, SQLException {
		TableData current = c.td;
		String sql = "SELECT * FROM " + current.table.name();
		String tablejoin = " ";
		for (TableData parent = current.parentTable,
				child = current; parent != null; child = parent, parent = parent.parentTable) {
			tablejoin += " INNER JOIN " + parent.table.name() + " ON " + child.table.name() + "." + child.parentTableFK
					+ " = " + parent.table.name() + "." + parent.pk.name();
		}
		sql += tablejoin;
		sql += c.getCriteriaText();
		if (this.show_querries)
			System.out.println(sql);
		return sql;
	}

	@Override
	public List<String> generateUpdateQuery(Criteria c, Object o)
			throws IllegalArgumentException, IllegalAccessException {
		TableData current = ClassMapper.getInstance().getTableData(o.getClass());
		List<String> query = new LinkedList<String>();
		for (TableData parent = current.parentTable,
				child = current; child != null; child = parent, parent = parent == null ? null : parent.parentTable) {
			if (child.lcd.size() == 0) {
				continue;
			}
			String sql = "UPDATE " + child.table.name();
			String sqlSet = " SET ";
			if (child.lcd.size() > 0) {
				sqlSet += child.lcd.get(0).col.name() + "=";
				if (child.lcd.get(0).f.get(o) instanceof String)
					sqlSet += "'" + child.lcd.get(0).f.get(o) + "'";
				else
					sqlSet += child.lcd.get(0).f.get(o);
			}
			for (int colInd = 1; colInd < child.lcd.size(); colInd++) {
				sqlSet += "," + child.lcd.get(colInd).col.name() + "=";
				if (child.lcd.get(colInd).f.get(o) instanceof String)
					sqlSet += "'" + child.lcd.get(colInd).f.get(o) + "'";
				else
					sqlSet += child.lcd.get(colInd).f.get(o);
			}
			sqlSet += c.getCriteriaText(child);
			if (this.show_querries)
				System.out.println(sql + sqlSet);
			query.add(sql + sqlSet + " ; ");
		}
		return query;
	}

	@Override
	public String generateDeleteQuery(Criteria c) {
		TableData current = c.td;
		String sql = " FROM " + current.table.name();
		String tablejoin = " ";
		String deleteSql = " DELETE ";
		List<String> tables = new LinkedList<>();
		for (TableData parent = current.parentTable,
				child = current; parent != null; child = parent, parent = parent.parentTable) {
			tables.add(parent.table.name());
			tablejoin += " INNER JOIN " + parent.table.name() + " ON " + child.table.name() + "." + child.parentTableFK
					+ " = " + parent.table.name() + "." + parent.pk.name();
		}
		/*
		 * for (int i = tables.size() - 1; i >= 0; i--) { deleteSql += " " +
		 * tables.get(i) + " , "; } deleteSql += " " + current.table.name() + " ";
		 */
		sql = deleteSql + sql;
		sql += tablejoin;
		sql += c.getCriteriaText();
		if (this.show_querries)
			System.out.println(sql);
		return sql;
	}

	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + this.dbPath);
		return con;
	}

	@Override
	public Criteria createCriteria(TableData current) {
		return new SQLiteCriteria(current);
	}

	@Override
	public String getDataBaseType(Type type) {
		if (type.equals(int.class) || type.equals(Integer.class))
			return "INTEGER";
		if (type.equals(float.class) || type.equals(Float.class))
			return "REAL";
		if (type.equals(boolean.class))
			return "NUMERIC";
		if (type.equals(String.class) || type.equals(char.class))
			return "TEXT";
		return null;
	}

	@Override
	public boolean create(Object o) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			TableData td = ClassMapper.getInstance().getTableData(o.getClass());
			Object generatedKey = null;
			List<TableData> tables = new LinkedList<TableData>();
			for (TableData parent = td.parentTable,
					child = td; child != null; child = parent, parent = parent == null ? null : parent.parentTable)
				tables.add(child);

			for (int i = tables.size() - 1; i >= 0; i--) {
				TableData child = tables.get(i);
				List<String> createQuery = this.generateCreateQuery(o, child.class_name);
				if (createQuery.size() == 0)
					return false;
				String keys[] = { child.pk.name() };
				String createsql = createQuery.get(0);
				if (generatedKey != null) {
					if (createQuery.get(0).contains(PARENT_FOREIGN_KEY)) {
						createsql = createsql.replace(PARENT_FOREIGN_KEY, generatedKey.toString());
					} else
						throw new ForeignKeyReplacementError(createsql);
				}

				PreparedStatement ps = con.prepareStatement(createsql, keys);
				ps.executeUpdate();

				ResultSet rs = ps.getGeneratedKeys();

				if (rs.next()) {
					generatedKey = rs.getObject(1);
				}

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Criteria c, Object o) {
		try {

			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			List<String> query = this.generateUpdateQuery(c, o);
			for (int i = query.size() - 1; i >= 0; i--)
				stmt.addBatch(query.get(i));
			stmt.executeBatch();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
