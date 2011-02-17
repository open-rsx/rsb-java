package rsb;

/**
 * This execption indicates that a method is called on an object
 * that is not in the correct state to perform the requested service.
 * For instance, it is not possible to create object with the XcfManager
 * if it is not correctly activated.
 * 
 * @author swrede
 *
 */
public class InvalidStateException extends RuntimeException {

	public InvalidStateException(String message) {
		super(message);
	}

	public InvalidStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidStateException(Throwable cause) {
		super(cause);
	}	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2396672331593990574L;

}
