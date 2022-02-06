package loader;

import java.sql.SQLException;
import java.util.List;

import criteria.Criteria;
import database.DBConnector;

public class ORMLoader {
	private DBConnector dbc;
	
	public ORMLoader(DBConnector dbc) {
		this.dbc=dbc;
	}
	
	public boolean createTable(Class<?> tableClass) {
		TableData td = ClassMapper.extractTableData(tableClass);
		return dbc.createTable(td);
	}
	
	public Criteria createCriteria(Class<?> tableClass) {
		return this.dbc.createCriteria(ClassMapper.extractTableData(tableClass));
	}
	
	public List<Object> get(Criteria c){
		try {
			return this.dbc.read(c.td, c);
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}
		
	}
}
