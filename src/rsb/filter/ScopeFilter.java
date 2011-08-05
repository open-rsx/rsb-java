/**
 *
 */
package rsb.filter;

import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ScopeFilter extends AbstractFilter {

	protected final static Logger log = Logger.getLogger(ScopeFilter.class.getName());

	private Scope scope;

	public ScopeFilter(final Scope scope) {
		super("ScopeFilter");
		this.scope = scope;
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(final FilterObserver observer, final FilterAction action) {
		observer.notify(this, action);
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return this.scope;
	}

	public void skip(EventId eventId) {
		log.info("Event with ID "
				+ eventId
				+ " will not be matched by ScopeFilter as this was already done by network layer!");
		super.skip(eventId);
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof ScopeFilter
				&& scope.equals(((ScopeFilter) that).getScope());
	}

	@Override
	public Event transform(final Event e) {
		log.fine("ScopeFilter with scope " + scope
				+ " received event to transform.");
		if (e.getScope() != null) {
			log.fine("  Event's receiver Scope = " + e.getScope());
		}
		boolean matches = false;
		String evtId = e.getId().toString();
		if (mustSkip(evtId)) {
			log.fine("event with ID " + evtId + " whitelisted in ScopeFilter!");
			matches = true;
			skipped(evtId);
		} else {
		    matches = (scope.equals(e.getScope())
			       || scope.isSuperScopeOf(e.getScope()));
		}
		if (matches) {
			log.fine("ScopeFilter matched successfully!");
		} else {
			log.fine("ScopeFilter rejected event!");
		}
		return matches ? e : null;
	}

}
