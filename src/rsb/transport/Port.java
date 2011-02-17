/**
 * 
 */
package rsb.transport;

import rsb.RSBEvent;
import rsb.RSBObject;

/**
 * @author swrede
 *
 */
public interface Port extends RSBObject {

    public void push(RSBEvent e);	
	
}
