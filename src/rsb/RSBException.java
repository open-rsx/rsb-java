package rsb;
/**
 * 
 */


/**
 * Superclass of all RSB exceptions that may be used in 
 * handlers that catch all RSB-related exceptions. 
 * 
 * @author swrede
 *
 */
public class RSBException extends Exception  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5223250815059688771L;

	/**
	 * 
	 */
	public RSBException() {
	}

	/**
	 * @param message
	 */
	public RSBException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RSBException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RSBException(String message, Throwable cause) {
		super(message, cause);
	}

}
