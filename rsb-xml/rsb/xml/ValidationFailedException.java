/**
 * 
 */
package rsb.xml;

import rsb.RSBException;
import rsb.transport.XOPData;

/**
 * @author jschaefe
 *
 */
public class ValidationFailedException extends RSBException {

	XOPData invalidData = null;
	
	public ValidationFailedException(String reason) {
		super(reason);
	}
	
	public ValidationFailedException(String reason, XOPData data) {
		super(reason);
		invalidData = data;
	}
	
	public XOPData getData() {
		return invalidData;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3632658303152950377L;

}
