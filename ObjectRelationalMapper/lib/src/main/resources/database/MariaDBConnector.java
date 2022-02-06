package database;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import criteria.Criteria;
import criteria.MariaDBCriteria;
import loader.ClassMapper;
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
	public List<Object> read(Criteria c) throws ClassNotFoundException, SQLException {
		TableData current = c.td;
		List<Object> lo = new LinkedList<Object>();
		Connection con = this.getConnection();
		Statement stmt = con.createStatement();
		String sql = "SELECT * FROM " + current.table.name();
		sql += " WHERE ";
		sql += c.getCriteriaText();
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
					if (current.pk != null && current.pk.autoincrement() && current.pk.name().contentEquals(col_name)) {
						Object obj = rs.getObject(i);
						current.pk_field.set(ret, obj);
						continue;
					}
					for (ColumnData cd : current.lcd) {
						if (cd.col != null && col_name.equals(cd.col.name())) {
							Object obj = rs.getObject(i);
							cd.f.set(ret, obj);
							break;
						}
						if (current.pk != null && col_name.equals(current.pk.name())) {
							Object obj = rs.getObject(i);
							current.pk_field.set(ret, obj);
							break;
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

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Criteria c) {
		try {
			TableData current = c.td;
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = "DELETE FROM " + current.table.name();
			sql += " WHERE ";
			sql += c.getCriteriaText();
			if (this.show_querries)
				System.out.println(sql);

			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery(sql);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean createTable(TableData current) {
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS " + current.table.name();
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

	@Override
	public boolean create(Object o) {
		try {
			TableData current = ClassMapper.getInstance().getTableData(o.getClass());
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO " + current.table.name();
			String decl = "", val = "";
			decl += " (";
			val += " (";

			if (current.lcd.get(0).f.get(o) instanceof String)
				val += "'" + current.lcd.get(0).f.get(o) + "'";
			else
				val += current.lcd.get(0).f.get(o);
			decl += current.lcd.get(0).col.name();
			for (int colInd = 1; colInd < current.lcd.size(); colInd++) {
				if (current.lcd.get(colInd).f.get(o) instanceof String)
					val += ",'" + current.lcd.get(colInd).f.get(o) + "'";
				else
					val += "," + current.lcd.get(colInd).f.get(o);
				decl += "," + current.lcd.get(colInd).col.name();
			}
			decl += ")";
			val += ")";
			sql += decl + " VALUES " + val;
			if (this.show_querries)
				System.out.println(sql);
			stmt.executeUpdate(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
