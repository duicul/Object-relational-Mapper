package loader;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import criteria.Criteria;
import database.DBConnector;
import exception.WrongColumnName;

public class ORMLoader {
	private DBConnector dbc;

	public ORMLoader(DBConnector dbc) {
		this.dbc = dbc;
	}

	public boolean createTable(Class<?> tableClass) {
		TableData td = ClassMapper.getInstance().getTableData(tableClass);
		return dbc.createTable(td);
	}

	public boolean dropTableStructure(Class<?> tableClass) {
		TableData td = ClassMapper.getInstance().getTableData(tableClass);
		return dbc.deleteTableHierarchy(td);
	}
	
	public boolean dropTable(Class<?> tableClass) {
		TableData td = ClassMapper.getInstance().getTableData(tableClass);
		return dbc.deleteTable(td);
	}

	public String getJSON(Object o) {
		Map<String, String> elements = this.getJSONMap(o);
		ObjectMapper objectMapper = new ObjectMapper();
		if (o == null)
			return "{null}";
		try {
			String json = objectMapper.writeValueAsString(elements);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "{}";
	}

	public Map<String, String> getJSONMap(Object o) {
		if (o == null)
			return null;
		TableData td = ClassMapper.getInstance().getTableData(o.getClass());
		Map<String, String> elements = new HashMap<String, String>();
		for (TableData current = td; current != null; current = current.parentTable) {
			for (int i = 0; i < current.lcd.size(); i++) {

				try {
					Object colVal = current.lcd.get(i).f.get(o);
					elements.put(current.lcd.get(i).col.name(), colVal.toString());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}

			}
			try {
				Object pkVal = current.pk_field.get(o);
				elements.put(current.pk.name(), pkVal.toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			for (TableData assocTab : current.associatedTables.keySet()) {
				ColumnData colDat = current.associatedTables.get(assocTab);
				if (colDat.oto != null) {
					Object objoto;
					try {
						objoto = current.associatedTables.get(assocTab).f.get(o);
						elements.put(assocTab.getAsForeignKey(), this.getJSON(objoto));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (colDat.otm != null) {
					Object objotm;
					try {
						objotm = current.associatedTables.get(assocTab).f.get(o);
						List<String> objsJSON = new LinkedList<String>();
						for (Object objind : (List<Object>) objotm)
							objsJSON.add(this.getJSON(objind));
						elements.put(assocTab.getAsForeignKey(), objsJSON.toString());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return elements;
	}

	public Criteria createCriteria(Class<?> tableClass) {
		return this.dbc.createCriteria(ClassMapper.getInstance().getTableData(tableClass));
	}

	public List<Object> get(Criteria c) {
		try {
			return this.dbc.read(c);
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}
	}

	public boolean insert(Object o) {
		return this.dbc.create(o,null,null);
	}

	public boolean delete(Criteria c) {
		return this.dbc.delete(c);
	}

	public boolean delete(Object o) {
		Criteria c = this.createCriteria(o.getClass());
		try {
			c.eq(c.td.pk.name(), c.td.pk_field.get(o));
		} catch (IllegalArgumentException | IllegalAccessException | WrongColumnName e) {
			e.printStackTrace();
			return false;
		}
		return this.delete(c);
	}

	public boolean updateSimple(Criteria c, Object o) {
		return this.dbc.updateSimple(c, o);
	}

	public boolean updateSimple(Object o) {
		Criteria c = this.createCriteria(o.getClass());
		try {
			for (TableData td = c.td; td != null; td = td.parentTable) {
				c.eq(td.pk.name(), td.pk_field.get(o));
			}
		} catch (IllegalArgumentException | IllegalAccessException | WrongColumnName e) {
			e.printStackTrace();
			return false;
		}
		return this.updateSimple(c, o);
	}
}
