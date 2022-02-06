package loader;

import java.lang.reflect.Field;

import annotation.PK;
import annotation.Table;

public abstract class TableInfo {
	public final Table table;
	public final PK pk;	
	public final Field pk_field;
	public final Class<?> class_name;
	public TableInfo(Table table,PK pk,Field pk_field,Class<?> class_name) {
		this.table=table;
		this.pk=pk;
		this.pk_field=pk_field;
		this.class_name=class_name;
	}
}