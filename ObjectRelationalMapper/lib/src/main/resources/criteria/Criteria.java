package criteria;

import java.util.LinkedList;
import java.util.List;

import loader.TableData;

public abstract class Criteria {
	public final TableData td;
	protected List<String> criteriaData;

	public Criteria(TableData td) {
		this.td = td;
		this.criteriaData = new LinkedList<String>();
	}

	protected void addCriteria(String criteria) {
		this.criteriaData.add(criteria);
	}

	public String getCriteriaText() {
		String critText = "";
		for (int i = 0; i < criteriaData.size() - 1; i++) {
			critText += criteriaData.get(i);
			critText += " AND ";
		}

		critText += criteriaData.get(criteriaData.size() - 1);

		return critText;
	}

	public abstract void gt(String column, Object val);

	public abstract void lt(String column, Object val);

	public abstract void eq(String column, Object val);

	public abstract void like(String column, Object val);
}
