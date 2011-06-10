/**
 * 
 */
package rsb;



/**
 * An InitializeException indicates erroneous situations during
 * the setup of the communication infrastructure. Usually, either 
 * the dispatcher nameservice can not be accessed, because it is
 * not running or the XCF.Initial.Host property is set to an incorrect
 * value. Other sources for this exception are related to the spread
 * group communication framework. Its properties can be changed via the
 * Spread.Host / Spread.Port and Spread.StartDaemon properties. Please 
 * inspect the value of getMessage() in order to check for the actual 
 * reason for this exception. 
 * 
 * @author swrede
 *
 */
public class InitializeException extends RSBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5223250815059688771L;

	/**
	 * 
	 */
	public InitializeException() {
	}

	/**
	 * @param message
	 */
	public InitializeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InitializeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializeException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
