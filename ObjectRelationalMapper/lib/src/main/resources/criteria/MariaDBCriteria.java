package criteria;

import exception.WrongColumnName;
import loader.ColumnData;
import loader.TableData;

public class MariaDBCriteria extends Criteria {

	public MariaDBCriteria(TableData td) {
		super(td);
	}
	
	public String checkColumnTable(String column) throws WrongColumnName {
		for (TableData current = this.td; current != null; current = current.parentTable) {
			for (ColumnData cd : current.lcd) {
				if (cd.col.name().equals(column))
					return current.table.name();
			}
			if (current.pk.name().equals(column))
				return current.table.name();
		}
		throw new WrongColumnName(td, column);
	}
	
	@Override
	public void gt(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(tableName + "." + column + " > '" + val+"'");
		}else {
			this.addCriteria(tableName + "." + column + " > " + val);
		}
	}

	@Override
	public void lt(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(tableName + "." + column + " < '" + val+"'");
		}else {
			this.addCriteria(tableName + "." + column + " < " + val);
		}
	}

	@Override
	public void eq(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(tableName + "." + column + " = '" + val+"'");
		}else {
			this.addCriteria(tableName + "." + column + " = " + val);
		}
	}

	@Override
	public void like(String column, Object val) throws WrongColumnName {
		String tableName = this.checkColumnTable(column);
		if(val instanceof String) {
			this.addCriteria(tableName + "." + column + " LIKE '" + val+"'");
		}else {
			this.addCriteria(tableName + "." + column + " LIKE " + val);
		}

	}

}
