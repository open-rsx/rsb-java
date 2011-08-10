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

	protected final static Logger LOG = Logger.getLogger(ScopeFilter.class.getName());

	private Scope scope;

	public ScopeFilter(final Scope scope) {
		super(ScopeFilter.class);
		this.scope = scope;
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public void dispachToObserver(final FilterObserver observer, final FilterAction action) {
		observer.notify(this, action);
	}

	public void setScope(final Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return this.scope;
	}

	@Override	
	public void skip(final EventId eventId) {
		LOG.info("Event with ID "
				+ eventId
				+ " will not be matched by ScopeFilter as this was already done by network layer!");
		super.skip(eventId);
	}

	@Override
	public boolean equals(final Object that) {
		return that instanceof ScopeFilter
				&& scope.equals(((ScopeFilter) that).getScope());
	}

	@Override
	public Event transform(final Event event) {
		LOG.fine("ScopeFilter with scope " + scope
				+ " received event to transform.");
		if (event.getScope() != null) {
			LOG.fine("  Event's receiver Scope = " + event.getScope());
		}
		boolean matches = false;
		if (mustSkip(event.getId())) {
			LOG.fine("event with ID " + event.getId() + " whitelisted in ScopeFilter!");
			matches = true;
			skipped(event.getId());
		} else {
		    matches = (scope.equals(event.getScope())
			       || scope.isSuperScopeOf(event.getScope()));
		}
		if (matches) {
			LOG.fine("ScopeFilter matched successfully!");
		} else {
			LOG.fine("ScopeFilter rejected event!");
		}
		return matches ? event : null;
	}

}
