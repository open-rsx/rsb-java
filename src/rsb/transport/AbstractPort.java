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
	public abstract void push(Event event) throws ConversionException;

}
