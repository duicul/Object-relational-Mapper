package loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import annotation.Column;
import annotation.OneToMany;
import annotation.OneToOne;
import annotation.PK;
import annotation.Table;

public class ClassMapper {
	private Map<Class<?>, TableData> cacheTableData;
	private static ClassMapper classMapper = null;

	private ClassMapper() {
		this.cacheTableData = new HashMap<Class<?>, TableData>();
	}

	public static ClassMapper getInstance() {
		if (classMapper == null)
			classMapper = new ClassMapper();
		return classMapper;
	}

	public TableData getTableData(Class<?> tableClass) {
		TableData td = this.cacheTableData.get(tableClass);
		if (td == null) {
			td = this.extractTableData(tableClass);
			this.cacheTableData.put(tableClass, td);
		}
		return td;
	}

	private TableData extractTableData(Class<?> tableClass) {
		Table t = getTableAnnotation(tableClass);
		if(t==null)
			return null;
		List<ColumnData> lcd = new ArrayList<ColumnData>();
		PK pk = null;
		Field pk_field = null;
		List<TableData> foreignTables = new LinkedList<TableData>();
		for (Field f : tableClass.getDeclaredFields()) {
			Column c = null;
			OneToMany otm = null;
			OneToOne oto = null;
			for (Annotation a : f.getDeclaredAnnotations()) {
				if (a instanceof Column) {
					c = (Column) a;
					continue;
				}
				if (a instanceof OneToMany) {
					otm = (OneToMany) a;
					Type list_oneto = f.getGenericType();
					if (list_oneto instanceof ParameterizedType) {
						ParameterizedType paramtype = (ParameterizedType) list_oneto;
						if (((ParameterizedType) list_oneto).getActualTypeArguments().length > 0)
							foreignTables.add(this.extractTableData((Class<?>) paramtype.getActualTypeArguments()[0]));
					}
					continue;
				}
				if (a instanceof OneToOne) {
					oto = (OneToOne) a;
					foreignTables.add(this.extractTableData((Class<?>) f.getGenericType()));
					continue;
				}
				if (a instanceof PK) {
					pk_field = f;
					pk = (PK) a;
					continue;
				}
			}
			if (pk == null)
				lcd.add(new ColumnData(c, otm, oto, f));
		}
		TableData parentTable = null;
		if (tableClass.getSuperclass() != Object.class) {
			parentTable = this.extractTableData(tableClass.getSuperclass());
			this.cacheTableData.put(tableClass.getSuperclass(), parentTable);
		}
		return new TableData(lcd, t, pk, pk_field, tableClass, parentTable, foreignTables);
	}

	public static Table getTableAnnotation(Class<?> tableClass) {
		for (Annotation a : tableClass.getAnnotations())
			if (a instanceof Table)
				return (Table) a;
		return null;
	}

}
