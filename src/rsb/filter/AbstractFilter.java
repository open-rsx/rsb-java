package rsb.filter;

import java.util.HashSet;
import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;

/**
 * @author swrede
 */
public abstract class AbstractFilter implements Filter {

	private final static Logger LOG = Logger.getLogger(AbstractFilter.class.getName());
	
	/* stores whitelisted event ids registered by skip() */
	protected HashSet<EventId> whitelist = new HashSet<EventId>();
	
	/* stores type info */
	protected String type = AbstractFilter.class.getSimpleName();

	/**
	 * This method does the actual filtering step. If an event can be 
	 * matched successfully against the condition of a specific filter, 
	 * it is returned and processed by further filtering steps. If null 
	 * is returned, the event is discarded.
	 */
	public abstract Event transform(Event e);
	
	protected AbstractFilter(String type) {
		this.type = type;
	}
	
	public AbstractFilter(Class<? extends AbstractFilter> type) {
		this.type = type.getSimpleName();
	}

	/**
	 * skip this filter for any event with the specified ID. This will cause the
	 * MTF to successfully transform the event without applying any checks.
	 */
	public void skip(EventId id) {		
		LOG.info("Event with ID " + id + " will not be matched by " + type + " as this was already done by network layer!");		
		whitelist.add(id);
	}
	
	/**
	 * returns whether events with the specified ID should be skipped or not.
	 * @param id
	 * @return true, if the event with the specified ID should be skipped
	 */
	public boolean mustSkip(EventId id) {
		return whitelist.contains(id);
	}
	
	/**
	 * remove ID from the list after the corresponding event has been skipped.
	 * @param id
	 */
	public void skipped(EventId id) {
		whitelist.remove(id);		
	}


	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public abstract void dispachToObserver(FilterObserver o, FilterAction a);
		
}
