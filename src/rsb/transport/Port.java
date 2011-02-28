/**
 * 
 */
package rsb.transport;

import rsb.RSBEvent;
import rsb.RSBObject;
import rsb.filter.FilterObserver;

/**
 * @author swrede
 *
 */
public interface Port extends RSBObject,FilterObserver {

    public void push(RSBEvent e);

	public String getType();

	public void setRouter(Router router);
	
}
