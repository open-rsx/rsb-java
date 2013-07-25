package rsb.transport;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rsb.Event;
import rsb.EventId;
import rsb.ParticipantId;
import rsb.Scope;

/**
 * Base class for a generic roundtrip check for Connectors.
 *
 * @author jwienke
 */
public abstract class ConnectorRoundtripCheck {

    private final int dataSize;

    protected ConnectorRoundtripCheck(final int dataSize) {
        this.dataSize = dataSize;
    }

    @Test(timeout = 4000)
    public void roundtrip() throws Throwable {

        final Scope scope = new Scope("/a/test/scope");

        final InPushConnector inPort = createInConnector();
        final OutConnector outPort = createOutConnector();

        final List<Event> receivedEvents = new ArrayList<Event>();

        inPort.addHandler(new EventHandler() {

            @Override
            public void handle(final Event event) {
                synchronized (receivedEvents) {
                    receivedEvents.add(event);
                    receivedEvents.notifyAll();
                }
            }

        });

        inPort.setScope(scope);
        inPort.activate();
        outPort.setScope(scope);
        outPort.activate();

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.dataSize; ++i) {
            builder.append('c');
        }

        final Event event = new Event(scope, String.class, builder.toString());
        event.setId(new ParticipantId(), 42);
        event.getMetaData().setUserInfo("foo", "a long string");
        event.getMetaData().setUserInfo("barbar", "a long string again");
        event.getMetaData().setUserTime("asdasd", 324234);
        event.getMetaData().setUserTime("xxx", 42);
        event.addCause(new EventId(new ParticipantId(), 23434));
        event.addCause(new EventId(new ParticipantId(), 42));

        outPort.push(event);

        synchronized (receivedEvents) {
            while (receivedEvents.size() != 1) {
                receivedEvents.wait();
            }

            final Event receivedEvent = receivedEvents.get(0);

            // normalize times as they are not important for this test
            event.getMetaData().setReceiveTime(
                    receivedEvent.getMetaData().getReceiveTime());

            assertEquals(event, receivedEvent);
        }

        inPort.deactivate();
        outPort.deactivate();

    }

    protected abstract OutConnector createOutConnector() throws Throwable;

    protected abstract InPushConnector createInConnector() throws Throwable;

}
