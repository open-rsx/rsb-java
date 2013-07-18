package rsb.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;

/**
 * An abstract test class to implement tests for connectors.
 *
 * @author jwienke
 */
public abstract class ConnectorCheck {

    private OutConnector outConnector;
    private static final Scope OUT_BASE_SCOPE = new Scope("/this");

    @Before
    public void setUp() throws Throwable {

        final OutConnector outConnector = createOutConnector();
        outConnector.setScope(OUT_BASE_SCOPE);
        outConnector.activate();
        this.setOutConnector(outConnector);

    }

    @After
    public void tearDown() throws Throwable {
        this.getOutConnector().deactivate();
    }

    @Test(expected = ConversionException.class)
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void noConverterPassedToClient() throws Throwable {
        final Event event = new Event(OUT_BASE_SCOPE, Throwable.class,
                new Throwable());
        event.setId(new ParticipantId(), 42);
        this.getOutConnector().push(event);
    }

    protected UnambiguousConverterMap<ByteBuffer> getConverterStrategy(
            final String key) {
        final UnambiguousConverterMap<ByteBuffer> strategy = new UnambiguousConverterMap<ByteBuffer>();
        strategy.addConverter(key, new StringConverter());
        return strategy;
    }

    @Test(timeout = 10000)
    public void hierarchicalSending() throws Throwable {

        final Scope sendScope = OUT_BASE_SCOPE.concat(new Scope(
                "/is/a/hierarchy"));

        final List<Scope> receiveScopes = sendScope.superScopes(true);

        // install event handlers for all receive scopes
        final Map<Scope, List<Event>> receivedEventsByScope = new HashMap<Scope, List<Event>>();
        final List<InPushConnector> inPorts = new ArrayList<InPushConnector>();
        for (final Scope scope : receiveScopes) {

            final List<Event> receivedEvents = new ArrayList<Event>();
            receivedEventsByScope.put(scope, receivedEvents);
            final InPushConnector inPort = createInConnector();
            inPort.addHandler(new EventHandler() {

                @Override
                public void handle(final Event event) {
                    synchronized (receivedEventsByScope) {
                        receivedEvents.add(event);
                        receivedEventsByScope.notifyAll();
                    }
                }

            });
            inPort.setScope(scope);
            inPort.activate();

            inPorts.add(inPort);

        }

        // TODO: Don't know why this is needed, sometimes spread is too slow?
        Thread.sleep(200);

        final int numEvents = 100;

        // send events
        final Event event = new Event(sendScope, String.class, "a test string "
                + numEvents);
        event.setId(new ParticipantId(), 42);

        this.getOutConnector().push(event);

        // wait for all receivers to get the scope
        for (final Scope scope : receiveScopes) {
            synchronized (receivedEventsByScope) {
                while (receivedEventsByScope.get(scope).size() != 1) {
                    receivedEventsByScope.wait();
                }

                final Event receivedEvent = receivedEventsByScope.get(scope)
                        .get(0);

                // normalize times as they are not important for this test
                event.getMetaData().setReceiveTime(
                        receivedEvent.getMetaData().getReceiveTime());

                assertEquals(event, receivedEvent);
            }
        }

        // deactivate ports
        for (final InPushConnector inPort : inPorts) {
            inPort.deactivate();
        }

    }

    protected abstract InPushConnector createInConnector() throws Throwable;

    protected abstract OutConnector createOutConnector() throws Throwable;

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void longGroupNames() throws Throwable {

        final Event event = new Event(String.class, "a test string");
        event.setScope(OUT_BASE_SCOPE
                .concat(new Scope(
                        "/is/a/very/long/scope/that/would/never/fit/in/a/spread/group/directly")));
        event.setId(new ParticipantId(), 452334);

        this.getOutConnector().push(event);

    }

    @Test(timeout = 10000)
    public void sendMetaData() throws Throwable {

        // create an event to send
        final Scope scope = OUT_BASE_SCOPE
                .concat(new Scope("/test/scope/again"));
        final Event event = new Event(scope, String.class, "a test string");
        event.setId(new ParticipantId(), 634);

        // create a receiver to wait for event
        final List<Event> receivedEvents = new ArrayList<Event>();
        final InPushConnector inPort = createInConnector();
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

        Thread.sleep(500);

        // send event
        final long beforeSend = System.currentTimeMillis() * 1000;
        this.getOutConnector().push(event);
        final long afterSend = System.currentTimeMillis() * 1000;

        assertTrue(event.getMetaData().getSendTime() >= beforeSend);
        assertTrue(event.getMetaData().getSendTime() <= afterSend);

        // wait for receiving the event
        synchronized (receivedEvents) {
            while (receivedEvents.size() != 1) {
                receivedEvents.wait();
            }

            final Event receivedEvent = receivedEvents.get(0);

            // first check that there is a receive time in the event
            assertTrue(receivedEvent.getMetaData().getReceiveTime() >= beforeSend);
            assertTrue(receivedEvent.getMetaData().getReceiveTime() >= receivedEvent
                    .getMetaData().getSendTime());
            assertTrue(receivedEvent.getMetaData().getReceiveTime() <= System
                    .currentTimeMillis() * 1000);

            // now adapt this time to use the normal equals method for comparing
            // all other fields
            event.getMetaData().setReceiveTime(
                    receivedEvent.getMetaData().getReceiveTime());
            receivedEvent.getMetaData().setSendTime(
                    event.getMetaData().getSendTime());

            assertEquals(event, receivedEvent);
        }

        inPort.deactivate();
    }

    protected OutConnector getOutConnector() {
        return this.outConnector;
    }

    protected void setOutConnector(final OutConnector outConnector) {
        this.outConnector = outConnector;
    }

}
