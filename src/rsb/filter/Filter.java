/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.Event;

/**
 * @author swrede
 */
public interface Filter {

	// public boolean isStateless();

	/**
	 * transform the given event into a result event according to this filters
	 * rules. This may return null, if the event is dropped by the filter, the
	 * passed-in event if no transformation is applied, or some new event.
	 * 
	 * @param e
	 *            the event to be transformed
	 * @return the transformed event or null
	 */
	public Event transform(Event e);

	// /**
	// * tell this MTF to skip any event matching the specified ID it
	// encounters.
	// * This causes the MTF to behave like an identity transformation and just
	// * return the event as-is when it's passed to transform().
	// *
	// * @param id
	// * the event ID to be skipped
	// * @see #transform(Event)
	// */
	// public void skip(EventId id);

	public boolean equals(Filter that);

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a);

}
