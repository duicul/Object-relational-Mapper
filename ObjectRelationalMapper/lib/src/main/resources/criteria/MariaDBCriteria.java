package criteria;

import exception.WrongColumnName;
import loader.ColumnData;
import loader.TableData;

public class MariaDBCriteria extends Criteria {

	public MariaDBCriteria(TableData td) {
		super(td);
	}
	
	public boolean checkColumnExists(String column) throws WrongColumnName {
		for(ColumnData cd :this.td.lcd) {
			if(cd.col.name().equals(column)) 
				return true;
		}
		if(td.pk.name().equals(column))
			return true;
		throw new WrongColumnName(td,column);
	}
	
	@Override
	public void gt(String column, Object val) throws WrongColumnName {
		this.checkColumnExists(column);
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " > '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " > " + val);
		}
	}

	@Override
	public void lt(String column, Object val) throws WrongColumnName {
		this.checkColumnExists(column);
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " < '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " < " + val);
		}
	}

	@Override
	public void eq(String column, Object val) throws WrongColumnName {
		this.checkColumnExists(column);
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " = '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " = " + val);
		}
	}

	@Override
	public void like(String column, Object val) throws WrongColumnName {
		this.checkColumnExists(column);
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " LIKE '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " LIKE " + val);
		}

	}

}
