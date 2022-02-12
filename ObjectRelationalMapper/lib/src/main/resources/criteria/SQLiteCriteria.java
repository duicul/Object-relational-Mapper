package criteria;

import loader.TableData;

public class SQLiteCriteria extends MariaDBCriteria {

	public SQLiteCriteria(TableData td) {
		super(td);
	}
	
	@Override
	public Criteria adaptForTable(TableData adaptTable) {
		Criteria c = new SQLiteCriteria(adaptTable);
		for(String[] crit : this.criteriaData)
			c.addCriteriaWithCheck(crit[0], crit[1]);
		return c;
	}
}
