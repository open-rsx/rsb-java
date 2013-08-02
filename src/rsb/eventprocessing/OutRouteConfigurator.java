package rsb.eventprocessing;

import rsb.Event;
import rsb.RSBException;
import rsb.transport.OutConnector;

/**
 * Implementing classes provide outgoing communication routes for
 * {@link rsb.Participant}s.
 *
 * @author jwienke
 */
public interface OutRouteConfigurator extends RouteConfigurator<OutConnector> {

    /**
     * Sends an event.
     *
     * This method must only be called after activating the object with
     * {@link #activate()}.
     *
     * @param event
     *            event to send
     * @throws RSBException
     *             sending error. e.g. impossible to convert data, transport
     *             error
     */
    void publishSync(Event event) throws RSBException;

}
