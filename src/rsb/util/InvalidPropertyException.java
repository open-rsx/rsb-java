package rsb.util;

public class InvalidPropertyException extends RuntimeException {

	public InvalidPropertyException() {
		super();
	}

	public InvalidPropertyException(String message) {
		super(message);
	}

	public InvalidPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPropertyException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5696967985100081449L;	
	
}
