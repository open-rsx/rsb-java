/**
 * 
 */
package rsb;

/**
 * @author swrede
 *
 */
public class EventProcessor {

	// TODO Think about the necessity if this object needs to be a thread object.
	
	// if invoked, the event is dispatched to subscribers, typically called by ports
    public void process(RSBEvent e) {
    	;
    }

    // add a subscription
    public void subscribe(Subscription s) {
    	;
    }

    // unsubscribe a subscription
    public void unsubscribe(Subscription s) {
    	;
    }
	
    public void start() {
    	
    }

    public void stop() {
    	
    }

}
