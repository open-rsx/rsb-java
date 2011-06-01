package rsb.filter;

import java.util.HashSet;
import java.util.logging.Logger;

import rsb.Event;
import rsb.EventId;


public abstract class AbstractFilter implements Filter {

	protected static Logger log = Logger.getLogger(AbstractFilter.class.getName());
	
	/* stores whitelisted event ids registered by skip() */
	protected HashSet<String> whitelist = new HashSet<String>();
	
	/* stores type info */
	protected String type = "AbstractFilter";
	
	public AbstractFilter (String type) {
        this.type=type;
    }

	public Event transform(Event e) {
		log.info("transform method matched event");
		// always matches, just returns original event
		return e;
	}

	/**
	 * skip this filter for any event with the specified ID. This will cause the
	 * MTF to successfully transform the event without applying any checks.
	 * @see net.sf.xcf.dtm.Element#skip(net.rsb.event.EventId)
	 */
	public void skip(EventId id) {
		skip(id.toString());
	}
	
	public void skip(String id) {
		whitelist.add(id);
	}
	
	/**
	 * returns whether events with the specified ID should be skipped or not.
	 * @param id
	 * @return true, if the event with the specified ID should be skipped
	 */
	public boolean mustSkip(EventId id) {
		return mustSkip(id.toString());
	}
	
	public boolean mustSkip(String id) {
		return whitelist.contains(id);
	}
	
	/**
	 * remove ID from the list after the corresponding event has been skipped.
	 * @param id
	 */
	public void skipped(EventId id) {
		whitelist.remove(id.toString());		
	}
	
	public void skipped(String id) {
		whitelist.remove(id);		
	}

	/**
	 * Helper method for double dispatch of Filter registrations
	 */
	public abstract void dispachToObserver(FilterObserver o, FilterAction a);
		
}
