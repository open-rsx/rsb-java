package rsb.eventprocessing;

import rsb.Event;
import rsb.Participant;
import rsb.converter.ConversionException;
import rsb.transport.OutConnector;

/**
 * Implementing classes provide outgoing communication routes for
 * {@link Participant}s.
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
     * @throws ConversionException
     *             unable to send the event because the contained data cannot be
     *             converted for the wire
     */
    void publishSync(Event event) throws ConversionException;

}
