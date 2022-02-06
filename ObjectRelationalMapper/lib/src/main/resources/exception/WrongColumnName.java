package exception;

import loader.TableData;

public class WrongColumnName extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7472002920178958126L;
	
	public WrongColumnName(TableData td, String columnName) {
		super("Table "+td.table.name()+" for "+td.class_name+" does not have the column "+columnName);
	}
}
