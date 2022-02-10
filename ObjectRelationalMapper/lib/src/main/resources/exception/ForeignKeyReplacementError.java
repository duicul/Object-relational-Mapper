package exception;

public class ForeignKeyReplacementError extends Exception {

	public ForeignKeyReplacementError(String message) {
		super("The foreign key is not present in the query "+message);
	}

}
