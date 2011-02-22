/**
 * 
 */
package rsb.transport;

import rsb.RSBEvent;
import rsb.RSBObject;
import rsb.transport.convert.StringConverter;

/**
 * @author swrede
 *
 */
public interface Port extends RSBObject {

    public void push(RSBEvent e);

	public String getType();
	
}
