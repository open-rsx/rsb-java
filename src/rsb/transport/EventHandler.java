package rsb.transport;

import rsb.RSBEvent;

/**
 * A generic interface for handlers of {@link RSBEvent}s.
 * 
 * @author jwienke
 */
public interface EventHandler {

	public void handle(RSBEvent e);

}