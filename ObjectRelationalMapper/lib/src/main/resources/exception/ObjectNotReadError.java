package exception;

public class ObjectNotReadError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1041812190128416897L;

	public ObjectNotReadError(String message) {
		super("Object with class "+message+" needs to be read first ");
	}
}
