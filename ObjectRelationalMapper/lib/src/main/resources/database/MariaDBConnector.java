package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import criteria.Criteria;
import criteria.MariaDBCriteria;
import loader.ColumnData;
import loader.TableData;

public class MariaDBConnector extends DBConnector {

	private long port;
	private String hostname, username, password, database, driver;
	private boolean show_querries;

	public MariaDBConnector(long port, String hostname, String username, String password, String database,
			boolean show_querries) {
		this.port = port;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.database = database;
		this.show_querries = show_querries;
		this.driver = "org.mariadb.jdbc.Driver";
	}

	@Override
	public boolean deleteTable(String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean create() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Object> read(TableData current, Criteria c) throws ClassNotFoundException, SQLException {
		List<Object> lo = new LinkedList<Object>();
		Connection con = this.getConnection();
		Statement stmt = con.createStatement();
		String sql = "SELECT * FROM " + current.table.name();
		sql += " WHERE ";
		sql +=c.getCriteriaText();
		/*try {
			String select_querry = "SELECT * FROM ";
			if (hierarchy != null) {
				boolean first = true;
				for (TableData td : hierarchy)
					if (td.table != null) {
						if (!first) {
							sql += " , ";
							delete_querry += " , ";
						}
						sql += td.table.name();
						delete_querry += td.table.name();
						first = false;
					}
			}
			boolean crits = false;

			if (criters.size() > 0) {
				crits = true;
				sql += " WHERE ";
				sql += " " + criters.get(0).getCriteria() + "  ";
				for (int i = 1; i < criters.size(); i++)
					sql += " AND " + criters.get(i).getCriteria() + "  ";
			}

			if (hierarchy != null) {
				if (hierarchy.size() > 1) {
					if (crits)
						sql += " AND ";
					else
						sql += " WHERE ";
					TableData tdmain = hierarchy.get(0), tdmain1 = hierarchy.get(1);
					if (tdmain != null)
						sql += tdmain.table.name() + "." + tdmain1.pk.name() + "=" + tdmain1.table.name() + "."
								+ tdmain1.pk.name();
					for (int i = 2; i < hierarchy.size(); i++) {
						TableData prev, curr;
						prev = hierarchy.get(i - 1);
						curr = hierarchy.get(i);
						if (prev != null && curr != null)
							sql += " AND " + prev.table.name() + "." + curr.pk.name() + "=" + curr.table.name() + "."
									+ curr.pk.name();
					}
				}
			}
			sql += order == null ? "" : order;
			Constructor<?> cons = current_table.class_name.getConstructor();
			Object ret;
			if (show_querries)
				System.out.println(select_querry + sql);
			List<Object> retlo = new ArrayList<Object>();
			if (!remove) {
				ResultSet rs = stmt.executeQuery(select_querry + sql);
				ResultSetMetaData rsmd = rs.getMetaData();
				int no_col = rsmd.getColumnCount();
				while (rs.next()) {
					ret = cons.newInstance();
					for (int i = 1; i <= no_col; i++) {
						String col_name = rsmd.getColumnName(i);
						if (current_table.pk != null && current_table.pk.autoincrement()
								&& current_table.pk.name().contentEquals(col_name)) {
							Object obj = rs.getObject(i);
							current_table.pk_field.set(ret, obj);
							continue;
						}
						for (ColumnData cd : current_table.lcd) {
							if (cd.col != null && col_name.equals(cd.col.name())) {
								Object obj = rs.getObject(i);
								cd.f.set(ret, obj);
								break;
							}
							if (current_table.pk != null && col_name.equals(current_table.pk.name())) {
								Object obj = rs.getObject(i);
								current_table.pk_field.set(ret, obj);
								break;
							}
						}
						for (TableData td : hierarchy) {
							if (td.pk != null && td.pk.autoincrement() && td.pk.name().contentEquals(col_name)) {
								Object obj = rs.getObject(i);
								td.pk_field.set(ret, obj);
								break;
							}
							for (ColumnData cd : td.lcd) {
								if (cd.col != null && col_name.equals(cd.col.name())) {
									Object obj = rs.getObject(i);
									cd.f.set(ret, obj);
									break;
								}
							}
						}
					}
					retlo.add(ret);
				}
			}
			if (remove) {
				if (show_querries)
					System.out.println(delete_querry + sql);
				stmt.executeUpdate(delete_querry + sql);
			}
			con.close();
			return retlo;
		} catch (Exception e) {
			return null;
		}*/
		return lo;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = "CREATE TABLE " + current.table.name();
			sql += "(";
			for (ColumnData cd : current.lcd) {
				String coltype = DBConnector.getDataBaseType(cd.f.getGenericType());
				if (coltype != null)
					if (cd.col != null)
						sql += cd.col.name() + " " + coltype + " , ";
				/*
				 * else if(cd.otm!=null) sql+=cd.otm.column()+" "+coltype+" , "; else
				 * if(cd.oto!=null) sql+=cd.oto.column()+" "+coltype+" , ";
				 */
			}
			if (current.pk != null) {
				sql += current.pk.name() + " " + current.pk.type() + " "
						+ (current.pk.autoincrement() ? "AUTO_INCREMENT" : "") + " , ";
				sql += " PRIMARY KEY ( " + current.pk.name() + " ) ";
			}
			for (TableData foreignTab : current.foreign_val_key) {
				sql += " , " + foreignTab.pk.name() + " " + foreignTab.pk.type();
			}

			sql += ");";
			if (this.show_querries)
				System.out.println(sql);
			stmt.executeUpdate(sql);
			con.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);
		Connection con = DriverManager.getConnection(
				"jdbc:mariadb://" + this.hostname + ":" + this.port + "/" + database + "?useSSL=false", username,
				password);
		return con;
	}

	@Override
	public Criteria createCriteria(TableData current) {
		return new MariaDBCriteria(current);
	}
}
