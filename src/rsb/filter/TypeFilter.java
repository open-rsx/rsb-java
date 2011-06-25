/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rsb.filter;

import rsb.Event;
import rsb.Id;

/**
 * 
 * @author swrede
 */
public class TypeFilter extends AbstractFilter {

	Class<?> type;

	public TypeFilter() {
		super("TypeFilter");
	}

	public TypeFilter(Class<?> c) {
		super("TypeFilter");
		type = c;
	}

	public void skip(Id id) {
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
		String evtId = e.getId().toString();
		if (mustSkip(evtId)) {
			LOG.info("event with ID " + evtId + " whitelisted in TypeFilter!");
			skipped(evtId);
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
