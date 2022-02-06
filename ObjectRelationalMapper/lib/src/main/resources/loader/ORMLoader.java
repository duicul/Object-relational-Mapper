package loader;

import java.sql.SQLException;
import java.util.List;

import criteria.Criteria;
import database.DBConnector;

public class ORMLoader {
	private DBConnector dbc;

	public ORMLoader(DBConnector dbc) {
		this.dbc = dbc;
	}

	public boolean createTable(Class<?> tableClass) {
		TableData td = ClassMapper.extractTableData(tableClass);
		return dbc.createTable(td);
	}

	public String getJSON(Object o) {
		TableData td = ClassMapper.extractTableData(o.getClass());
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
		return this.dbc.createCriteria(ClassMapper.extractTableData(tableClass));
	}

	public List<Object> get(Criteria c) {
		try {
			return this.dbc.read(c.td, c);
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}

	}

	public boolean insert(Object o) {
		TableData td = ClassMapper.extractTableData(o.getClass());
		return this.dbc.create(td, o);
	}
}
