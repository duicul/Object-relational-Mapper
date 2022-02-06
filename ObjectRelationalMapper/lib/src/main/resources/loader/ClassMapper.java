package loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import annotation.Column;
import annotation.OneToMany;
import annotation.OneToOne;
import annotation.PK;
import annotation.Table;

public class ClassMapper {

	public ClassMapper() {
		// TODO Auto-generated constructor stub
	}

	public static TableData extractTableData(Class<?> tableClass) {
		Table t = getTableAnnotation(tableClass);
		List<ColumnData> lcd = new ArrayList<ColumnData>();
		PK pk = null;
		Field pk_field = null;
		List<TableData> foreign_keys = new ArrayList<TableData>();
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
							foreign_keys.add(ClassMapper.extractTableData((Class<?>) paramtype.getActualTypeArguments()[0]));
					}
					continue;
				}
				if (a instanceof OneToOne) {
					oto = (OneToOne) a;
					foreign_keys.add(ClassMapper.extractTableData((Class<?>) f.getGenericType()));
					continue;
				}
				if (a instanceof PK) {
					pk_field = f;
					pk = (PK) a;
					continue;
				}
			}
			lcd.add(new ColumnData(c, otm, oto, f));
		}
		return new TableData(lcd, t, pk, pk_field, tableClass, foreign_keys);

	}

	public static Table getTableAnnotation(Class<?> tableClass) {
		for (Annotation a : tableClass.getAnnotations())
			if (a instanceof Table)
				return (Table) a;
		return null;
	}

}
