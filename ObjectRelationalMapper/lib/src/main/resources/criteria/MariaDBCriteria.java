package criteria;

import loader.TableData;

public class MariaDBCriteria extends Criteria {

	public MariaDBCriteria(TableData td) {
		super(td);
	}

	@Override
	public void gt(String column, Object val) {
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " > '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " > " + val);
		}
	}

	@Override
	public void lt(String column, Object val) {
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " < '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " < " + val);
		}
	}

	@Override
	public void eq(String column, Object val) {
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " = '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " = " + val);
		}
	}

	@Override
	public void like(String column, Object val) {
		if(val instanceof String) {
			this.addCriteria(td.table.name() + "." + column + " LIKE '" + val+"'");
		}else {
			this.addCriteria(td.table.name() + "." + column + " LIKE " + val);
		}

	}

}
