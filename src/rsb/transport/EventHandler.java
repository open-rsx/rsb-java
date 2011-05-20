package rsb.transport;

import rsb.Event;

/**
 * A generic interface for handlers of {@link Event}s.
 * 
 * @author jwienke
 */
public interface EventHandler {

	public void handle(Event e);

}