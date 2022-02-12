package loader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import annotation.PK;
import annotation.Table;

public class TableData extends TableInfo {
	public final List<ColumnData> lcd;
	public final Map<TableData,ForeignTable> associatedTables;

	public final TableData parentTable;
	public final String parentTableFK;

	public TableData(List<ColumnData> lcd, Table table, PK pk, Field pk_field, Class<?> class_name,
			TableData parentTable, Map<TableData,ForeignTable> associatedTables) {
		super(table, pk, pk_field, class_name);
		this.lcd = lcd;
		this.associatedTables = associatedTables;
		this.parentTable = parentTable;
		if (parentTable != null)
			this.parentTableFK = parentTable.table.name() + parentTable.pk.name();
		else
			this.parentTableFK = null;
	}
	
	public String getAsForeignKey() {
		return this.table.name()+this.pk.name();
	}

	public boolean containsColumn(String col) {
		for (ColumnData cd : this.lcd)
			if (cd.col.name().equals(col))
				return true;
		if (this.pk.name().equals(col))
			return true;
		return false;
	}
}
