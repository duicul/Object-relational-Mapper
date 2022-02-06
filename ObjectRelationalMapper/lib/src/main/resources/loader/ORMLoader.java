package loader;

import java.sql.SQLException;
import java.util.List;

import criteria.Criteria;
import database.DBConnector;
import exception.WrongColumnName;

public class ORMLoader {
	private DBConnector dbc;

	public ORMLoader(DBConnector dbc) {
		this.dbc = dbc;
	}

	public boolean createTable(Class<?> tableClass) {
		TableData td = ClassMapper.getInstance().getTableData(tableClass);
		return dbc.createTable(td);
	}
	
	public boolean dropTable(Class<?> tableClass) {
		TableData td = ClassMapper.getInstance().getTableData(tableClass);
		return dbc.deleteTable(td);
	}

	public String getJSON(Object o) {
		TableData td = ClassMapper.getInstance().getTableData(o.getClass());
		String json = "{";
		try {
			json += "'" + td.lcd.get(0).col.name() + "':";
			Object colVal = td.lcd.get(0).f.get(o);
			if (colVal instanceof String)
				json += "'" + colVal + "'";
			else
				json += colVal;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < td.lcd.size(); i++) {

			try {
				json += ",'" + td.lcd.get(i).col.name() + "':";
				Object colVal = td.lcd.get(i).f.get(o);
				if (colVal instanceof String)
					json += "'" + colVal + "'";
				else
					json += colVal;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				continue;
			}

		}
		try {
			json += ",'" + td.pk.name() + "':";
			Object pkVal = td.pk_field.get(o);
			if (pkVal instanceof String)
				json += "'" + pkVal + "'";
			else
				json += pkVal;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		json += "}";
		return json;
	}

	public Criteria createCriteria(Class<?> tableClass) {
		return this.dbc.createCriteria(ClassMapper.getInstance().getTableData(tableClass));
	}

	public List<Object> get(Criteria c) {
		try {
			return this.dbc.read(c);
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	public boolean insert(Object o) {
		return this.dbc.create(o);
	}
	
	public boolean delete(Criteria c) {
		return this.dbc.delete(c);
	}
	
	public boolean update(Criteria c, Object o) {
		return this.dbc.update(c, o);
	}
	
	public boolean update(Object o) {
		Criteria c =this.createCriteria(o.getClass());
		try {
			c.eq(c.td.pk.name(), c.td.pk_field.get(o));
		} catch (IllegalArgumentException | IllegalAccessException | WrongColumnName e) {
			e.printStackTrace();
			return false;
		}
		return this.update(c, o);		
	}
}
