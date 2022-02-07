package loader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import annotation.PK;
import annotation.Table;

public class TableData extends TableInfo {
	public final List<ColumnData> lcd;
	public final List<TableData> foreign_val_key;

	public final TableData parentTable;
	public final String parentTableFK;

	public TableData(List<ColumnData> lcd, Table table, PK pk, Field pk_field, Class<?> class_name,
			TableData parentTable, List<TableData> foreign_val_key) {
		super(table, pk, pk_field, class_name);
		this.lcd = lcd;
		this.foreign_val_key = foreign_val_key;
		this.parentTable = parentTable;
		if (parentTable != null)
			this.parentTableFK = parentTable.table.name() + parentTable.pk.name();
		else
			this.parentTableFK = null;
	}

	public boolean addForeignComposition(TableData tab) {
		if (tab instanceof TableData) {
			TableData tv = (TableData) tab;
			// if (foreign_val_key == null)
			// foreign_val_key = new ArrayList<TableData>();
			this.foreign_val_key.add(tv);
			return true;
		}
		return false;
	}
}
