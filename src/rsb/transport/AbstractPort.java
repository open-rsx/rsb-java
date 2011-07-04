package rsb.transport;

import rsb.Event;
import rsb.converter.ConversionException;
import rsb.filter.AbstractFilterObserver;

/**
 * 
 * @author swrede
 */
public abstract class AbstractPort extends AbstractFilterObserver implements
		Port {

	@Override
	public void push(Event e) throws ConversionException {
	}

}
