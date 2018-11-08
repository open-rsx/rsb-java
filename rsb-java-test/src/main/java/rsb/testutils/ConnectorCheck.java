/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.DoubleConverter;
import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;
import rsb.transport.EventHandler;
import rsb.transport.InConnector;
import rsb.transport.OutConnector;
import rsb.util.ExactTime;

// CHECKSTYLE.OFF: JavadocMethod - test class
// CHECKSTYLE.OFF: MagicNumber - random test values

/**
 * An abstract test class to implement tests for connectors.
 *
 * For reusability-reasons, this class does extend RsbTestCase, which uses
 * opinionated default for testing the main framework. Instead, the appropriate
 * JUnit rules are applied manually or rebuilt with changeable defaults.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class ConnectorCheck extends LoggingTestCase {

    private static final String UTF8_WIRE_SCHEMA = "utf-8-string";
    private static final Scope OUT_BASE_SCOPE = new Scope("/this");

    // CHECKSTYLE.OFF: VisibilityModifier - required by junit
    /**
     * Optionally, sleep between test cases.
     */
    @Rule
    public final SleepRule sleeper = new SleepRule();
    // CHECKSTYLE.ON: VisibilityModifier

    private OutConnector outConnector;

    @Before
    public void setUp() throws Throwable {

        final OutConnector outConnector = createOutConnector(
                getStringConverterStrategy(String.class.getName()));
        outConnector.setScope(OUT_BASE_SCOPE);
        outConnector.activate();
        this.setOutConnector(outConnector);

    }

    @After
    public void tearDown() throws Throwable {
        this.getOutConnector().deactivate();
    }

    @Test(expected = ConversionException.class, timeout = 10000)
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public void noConverterPassedToClient() throws Throwable {
        final Event event =
                new Event(OUT_BASE_SCOPE, Throwable.class, new Throwable());
        event.setId(new ParticipantId(), 42);
        this.getOutConnector().push(event);
    }

    private UnambiguousConverterMap<ByteBuffer>
            getStringConverterStrategy(final String key) {
        final UnambiguousConverterMap<ByteBuffer> strategy =
                new UnambiguousConverterMap<ByteBuffer>();
        strategy.addConverter(key, new StringConverter());
        return strategy;
    }

    private UnambiguousConverterMap<ByteBuffer>
            getDoubleConverterStrategy(final String key) {
        final UnambiguousConverterMap<ByteBuffer> strategy =
                new UnambiguousConverterMap<ByteBuffer>();
        strategy.addConverter(key, new DoubleConverter());
        return strategy;
    }

    @Test(timeout = 10000)
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void primitiveTypeSending() throws Throwable {
        final OutConnector outConnector =
                createOutConnector(getDoubleConverterStrategy(
                        DoubleConverter.SIGNATURE.getDataType().getName()));
        final InConnector inConnector =
                createInConnector(getDoubleConverterStrategy(
                        DoubleConverter.SIGNATURE.getSchema()));
        try {
            final Scope scope =
                    new Scope("/connectorCheck/primitiveTypeSending");

            final Object synchronizer = new Object();
            final List<Event> receivedEvents = new ArrayList<Event>();
            inConnector.addHandler(new EventHandler() {

                @Override
                public void handle(final Event event) {
                    synchronized (synchronizer) {
                        receivedEvents.add(event);
                        synchronizer.notifyAll();
                    }
                }

            });

            outConnector.setScope(scope);
            outConnector.activate();
            inConnector.setScope(scope);
            inConnector.activate();

            final double data = 45;
            final Event event = new Event(scope, Double.class, data);
            event.setId(new ParticipantId(), 23423);
            outConnector.push(event);

            synchronized (synchronizer) {
                while (receivedEvents.isEmpty()) {
                    synchronizer.wait();
                }
            }

            assertEquals(1, receivedEvents.size());
            assertEquals(data, receivedEvents.get(0).getData());

        } finally {
            try {
                outConnector.deactivate();
            } catch (final Exception e) {
                // ignore possible errors during attempts to recover as good as
                // possible
            }
            try {
                inConnector.deactivate();
            } catch (final Exception e) {
                // ignore possible errors during attempts to recover as good as
                // possible
            }
        }
    }

    @Test(timeout = 10000)
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void hierarchicalSending() throws Throwable {

        final Scope sendScope =
                OUT_BASE_SCOPE.concat(new Scope("/is/a/hierarchy"));

        final List<Scope> receiveScopes = sendScope.superScopes(true);

        // install event handlers for all receive scopes
        final Map<Scope, List<Event>> receivedEventsByScope =
                new HashMap<Scope, List<Event>>();
        final List<InConnector> inPorts = new ArrayList<InConnector>();
        for (final Scope scope : receiveScopes) {

            final List<Event> receivedEvents = new ArrayList<Event>();
            receivedEventsByScope.put(scope, receivedEvents);
            final InConnector inPort = createInConnector(
                    getStringConverterStrategy(UTF8_WIRE_SCHEMA));
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
        final Event event = new Event(sendScope, String.class,
                "a test string " + numEvents);
        event.setId(new ParticipantId(), 42);

        this.getOutConnector().push(event);

        // wait for all receivers to get the scope
        for (final Scope scope : receiveScopes) {
            synchronized (receivedEventsByScope) {
                while (receivedEventsByScope.get(scope).size() != 1) {
                    receivedEventsByScope.wait();
                }

                final Event receivedEvent =
                        receivedEventsByScope.get(scope).get(0);

                // normalize times as they are not important for this test
                event.getMetaData().setReceiveTime(
                        receivedEvent.getMetaData().getReceiveTime());

                assertEquals(event, receivedEvent);
            }
        }

        // deactivate ports
        for (final InConnector inPort : inPorts) {
            inPort.deactivate();
        }

    }

    protected abstract InConnector createInConnector(
            final UnambiguousConverterMap<ByteBuffer> converters)
                    throws Throwable;

    protected abstract OutConnector createOutConnector(
            final UnambiguousConverterMap<ByteBuffer> converters)
                    throws Throwable;

    @Test(timeout = 10000)
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void longGroupNames() throws Throwable {

        final Event event = new Event(String.class, "a test string again");
        event.setScope(OUT_BASE_SCOPE
                .concat(new Scope("/is/a/very/long/scope/that/would/never/fit"
                        + "/in/a/spread/group/directly")));
        event.setId(new ParticipantId(), 452334);

        this.getOutConnector().push(event);

    }

    @Test(timeout = 10000)
    public void sendMetaData() throws Throwable {

        // create an event to send
        final Scope scope =
                OUT_BASE_SCOPE.concat(new Scope("/test/scope/again"));
        final Event event = new Event(scope, String.class, "a test string");
        event.setId(new ParticipantId(), 634);

        // create a receiver to wait for event
        final List<Event> receivedEvents = new ArrayList<Event>();
        final InConnector inPort =
                createInConnector(getStringConverterStrategy(UTF8_WIRE_SCHEMA));
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
        final long beforeSend = ExactTime.currentTimeMicros();
        this.getOutConnector().push(event);
        final long afterSend = ExactTime.currentTimeMicros();

        assertTrue(event.getMetaData().getSendTime() >= beforeSend);
        assertTrue(event.getMetaData().getSendTime() <= afterSend);

        // wait for receiving the event
        synchronized (receivedEvents) {
            while (receivedEvents.size() != 1) {
                receivedEvents.wait();
            }

            final Event receivedEvent = receivedEvents.get(0);

            // first check that there is a receive time in the event
            assertTrue(
                    receivedEvent.getMetaData().getReceiveTime() >= beforeSend);
            assertTrue(receivedEvent.getMetaData()
                    .getReceiveTime() >= receivedEvent.getMetaData()
                            .getSendTime());
            assertTrue(receivedEvent.getMetaData()
                    .getReceiveTime() <= ExactTime.currentTimeMicros());

            // now adapt this time to use the normal equals method for comparing
            // all other fields
            event.getMetaData().setReceiveTime(
                    receivedEvent.getMetaData().getReceiveTime());
            receivedEvent.getMetaData()
                    .setSendTime(event.getMetaData().getSendTime());

            assertEquals(event, receivedEvent);
        }

        inPort.deactivate();
    }

    @Test(timeout = 10000)
    public void noMoreEventsAfterDeactivate() throws Throwable {

        // create an event to send
        final Scope scope =
                OUT_BASE_SCOPE.concat(new Scope("/test/deactivate/stuff"));
        final Event event = new Event(scope, String.class, "a test string 2");
        event.setId(new ParticipantId(), 634);

        // create a receiver to wait for event
        final List<Event> receivedEvents = new ArrayList<Event>();
        final InConnector inPort =
                createInConnector(getStringConverterStrategy(UTF8_WIRE_SCHEMA));
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

        // deactivate the in connector and send an event. It must not be
        // received
        inPort.deactivate();

        Thread.sleep(500);

        getOutConnector().push(event);

        Thread.sleep(500);

        synchronized (receivedEvents) {
            assertTrue(receivedEvents.isEmpty());
        }

    }

    @Test
    public void hasTransportUri() throws Throwable {
        assertNotNull(this.outConnector.getTransportUri());
        final InConnector inConnector =
                createInConnector(getStringConverterStrategy(UTF8_WIRE_SCHEMA));
        inConnector.setScope(new Scope("/some/strange/scope"));
        inConnector.activate();
        try {
            assertNotNull(this.outConnector.getTransportUri());
        } finally {
            inConnector.deactivate();
        }
    }

    protected OutConnector getOutConnector() {
        return this.outConnector;
    }

    protected void setOutConnector(final OutConnector outConnector) {
        this.outConnector = outConnector;
    }

}

// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocMethod
