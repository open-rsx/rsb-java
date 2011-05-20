/**
 * 
 */
package rsb.transport;

import rsb.Event;
import rsb.RSBObject;
import rsb.filter.FilterObserver;

/**
 * @author swrede
 */
public interface Port extends RSBObject, FilterObserver {

	public void push(Event e);

	public String getType();

}
