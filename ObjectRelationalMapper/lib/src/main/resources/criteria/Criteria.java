package criteria;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import exception.WrongColumnName;
import loader.ColumnData;
import loader.TableData;

public abstract class Criteria {
	public final TableData td;
	protected List<String[]> criteriaData;

	public Criteria(TableData td) {
		this.td = td;
		this.criteriaData = new LinkedList<String[]>();
	}

	public abstract Criteria adaptForTable(TableData adaptTable);

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

	protected void addCriteria(String column, String criteria) {
		String critData[] = { column, criteria };
		this.criteriaData.add(critData);
	}

	protected boolean addCriteriaWithCheck(String column, String criteria) {
		try {
			this.checkColumnTable(column);
			this.addCriteria(column, criteria);
			return true;
		} catch (WrongColumnName e) {
			return false;
		}

	}

	public String getCriteriaText(TableData appliedTable) {
		String critText = "";
		List<String> filteredCriteria = new LinkedList<String>();
		for (String[] critData : this.criteriaData) {
			if (appliedTable == null)
				filteredCriteria.add(critData[1]);
			else if (appliedTable.containsColumn(critData[0]))
				filteredCriteria.add(critData[1]);
		}
		for (int i = 0; i < filteredCriteria.size() - 1; i++) {
			critText += filteredCriteria.get(i);
			critText += " AND ";
		}
		if (filteredCriteria.size() > 0)
			critText += filteredCriteria.get(filteredCriteria.size() - 1);
		if (critText.length() != 0) {
			critText = " WHERE " + critText;
		}
		return critText;
	}

	public String getCriteriaText() {
		return this.getCriteriaText(null);
	}

	public abstract void gt(String column, Object val) throws WrongColumnName;

	public abstract void lt(String column, Object val) throws WrongColumnName;

	public abstract void eq(String column, Object val) throws WrongColumnName;

	public abstract void like(String column, Object val) throws WrongColumnName;

	public void addForeignTableCriteria(TableData adaptTable, Object objval) {
		String column = adaptTable.getAsForeignKey();
		try {
			Object val = adaptTable.pk_field.get(objval);
			if(val instanceof String)
				this.addCriteria(column, td.table.name() + "." + column + " = '" + val+"'");
			else
				this.addCriteria(column, td.table.name() + "." + column + " = " + val);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
