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
	public void eqConstant(TableData td,String column, Object val) throws WrongColumnName {
		boolean found =false;
		for (TableData current = this.td; current != null; current = current.parentTable) {
			if(current.table.name().equals(td.table.name()))
				found=true;
		}
		if(found)
			this.addCriteria(column,td.table.name() + "." + column + " = " + val);
		else
			throw new WrongColumnName(td, column);
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

	@Override
	public Criteria adaptForTable(TableData adaptTable) {
		Criteria c = new MariaDBCriteria(adaptTable);
		for(String[] crit : this.criteriaData)
			c.addCriteriaWithCheck(crit[0], crit[1]);
		return c;
	}

	

}
