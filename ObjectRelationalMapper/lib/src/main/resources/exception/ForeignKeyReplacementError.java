package exception;

public class ForeignKeyReplacementError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2173942680074174839L;

	public ForeignKeyReplacementError(String message) {
		super("The foreign key is not present in the query "+message);
	}

}
