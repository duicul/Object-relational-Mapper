package criteria;

import exception.WrongColumnName;
import loader.ColumnData;
import loader.TableData;

public class MariaDBCriteria extends Criteria {

	public MariaDBCriteria(TableData td) {
		super(td);
	}
	
	@Override
	public void gt(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(column,tableName + "." + column + " > '" + val+"'");
		}else {
			this.addCriteria(column,tableName + "." + column + " > " + val);
		}
	}

	@Override
	public void lt(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(column,tableName + "." + column + " < '" + val+"'");
		}else {
			this.addCriteria(column,tableName + "." + column + " < " + val);
		}
	}

	@Override
	public void eq(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(column,tableName + "." + column + " = '" + val+"'");
		}else {
			this.addCriteria(column,tableName + "." + column + " = " + val);
		}
	}

	@Override
	public void like(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(column,tableName + "." + column + " LIKE '" + val+"'");
		}else {
			this.addCriteria(column,tableName + "." + column + " LIKE " + val);
		}

	}

}
