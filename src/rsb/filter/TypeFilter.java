/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;

/**
 * 
 * @author swrede
 */
public class TypeFilter extends AbstractFilter {

	private final static Logger LOG = Logger.getLogger(TypeFilter.class.getName()); 
	
	Class<?> type;

	public TypeFilter() {
		super(TypeFilter.class);
	}

	public TypeFilter(Class<?> c) {
		super(TypeFilter.class);
		type = c;
	}

	public void skip(EventId id) {
		LOG.info("Event with ID "
				+ id
				+ " will not be matched by TypeFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof TypeFilter
				&& type.equals(((TypeFilter) that).type);
	}

	@Override
	public Event transform(Event e) {
		// check skip
		EventId eventId = e.getId();
		if (mustSkip(eventId)) {
			LOG.info("event with ID " + eventId + " whitelisted in TypeFilter!");
			skipped(eventId);
			return e;
		}
		// condition: class types are equal
		if (type.isAssignableFrom(e.getClass())) {
			return e;
		} else {
			return null;
		}
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}
}
