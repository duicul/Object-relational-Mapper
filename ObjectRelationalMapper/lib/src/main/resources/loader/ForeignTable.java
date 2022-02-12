package loader;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import annotation.Column;
import annotation.OneToMany;
import annotation.OneToOne;

public class ForeignTable extends ColumnData {
	public final Class<?> foreignClass;
	public ForeignTable(Column col, OneToMany otm, OneToOne oto, Field f,Class<?> foreignClass) {
		super(col, otm, oto, f);
		this.foreignClass = foreignClass;
	}
	
	public List<Object> getObjectsFromParent(Object o){
		List<Object> retObj = new LinkedList<Object>();
		if(oto != null) {
			try {
				retObj.add(this.f.get(o));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return retObj;
		}
		if(otm != null) {
			try {
				Object obj = this.f.get(o);
				if(obj instanceof List<?>)
					retObj = (List<Object>) this.f.get(o);
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				
			}
			return retObj;
		}
		return retObj;
	}

}
