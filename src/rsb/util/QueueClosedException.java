/**
 * 
 */
package rsb.util;

/**
 * @author rgaertne
 *
 */
/**
 * Unchecked Exception that signals not allowed attempts to access an already 
 * closed queue, like pushing more elements in it or retrieving elements on an 
 * empty and closed queue.  
 */
public class QueueClosedException extends RuntimeException {

	
	private static final long serialVersionUID = 8241133545388210233L;

	
	/**
	 * Creates a QueueClosedException with the specified detail message.
	 * @param message detail message
	 */
	public QueueClosedException(String message) {
		super(message);
	}
	
}
