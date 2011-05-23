package rsb.transport;

import rsb.Event;
import rsb.filter.AbstractFilterObserver;

/**
 * 
 * @author swrede
 */
public abstract class AbstractPort extends AbstractFilterObserver implements
		Port {

	public void push(Event e) {
	}

}
