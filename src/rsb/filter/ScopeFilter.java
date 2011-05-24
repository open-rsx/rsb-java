/**
 *
 */
package rsb.filter;

import java.util.logging.Logger;

import rsb.Event;
import rsb.Scope;
import rsb.event.EventId;

/**
 * @author swrede
 *
 */
public class ScopeFilter extends AbstractFilter {

	protected static Logger log = Logger.getLogger(ScopeFilter.class.getName());

	private Scope scope;

	public ScopeFilter(Scope scope) {
		super("ScopeFilter");
		this.scope = scope;
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(FilterObserver o, FilterAction a) {
		o.notify(this, a);
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return this.scope;
	}

	public void skip(EventId id) {
		log.info("Event with ID "
				+ id
				+ " will not be matched by ScopeFilter as this was already done by network layer!");
		super.skip(id);
	}

	@Override
	public boolean equals(Filter that) {
		return that instanceof ScopeFilter
				&& scope.equals(((ScopeFilter) that).getScope());
	}

	@Override
	public Event transform(Event e) {
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
		    matches = (scope.equal(e.getScope())
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
