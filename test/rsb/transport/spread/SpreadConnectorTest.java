/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
package rsb.transport.spread;

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
import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;
import rsb.transport.EventHandler;

/**
 * @author jwienke
 */
public class SpreadConnectorTest {

    private SpreadWrapper outWrapper;
    private SpreadOutConnector outPort;
    private static final Scope OUT_BASE_SCOPE = new Scope("/this");

    @Before
    public void setUp() throws Throwable {

        this.outWrapper = Utilities.createSpreadWrapper();
        this.outPort = new SpreadOutConnector(
                this.outWrapper,
                this.getConverterStrategy(String.class.getName()),
                new QualityOfServiceSpec(Ordering.ORDERED, Reliability.RELIABLE));
        this.outPort.setScope(OUT_BASE_SCOPE);
        this.outPort.activate();

    }

    private UnambiguousConverterMap<ByteBuffer> getConverterStrategy(
            final String key) {
        final UnambiguousConverterMap<ByteBuffer> strategy = new UnambiguousConverterMap<ByteBuffer>();
        strategy.addConverter(key, new StringConverter());
        return strategy;
    }

    @After
    public void tearDown() throws Throwable {
        this.outPort.deactivate();
    }

    @Test(expected = ConversionException.class)
    public void noConverterPassedToClient() throws Throwable {
        final Event event = new Event(OUT_BASE_SCOPE, Throwable.class,
                new Throwable());
        event.setId(new ParticipantId(), 42);
        this.outPort.push(event);
    }

    @Test(timeout = 10000)
    public void hierarchicalSending() throws Throwable {

        final Scope sendScope = OUT_BASE_SCOPE.concat(new Scope(
                "/is/a/hierarchy"));

        final List<Scope> receiveScopes = sendScope.superScopes(true);

        // install event handlers for all receive scopes
        final Map<Scope, List<Event>> receivedEventsByScope = new HashMap<Scope, List<Event>>();
        final List<SpreadInPushConnector> inPorts = new ArrayList<SpreadInPushConnector>();
        for (final Scope scope : receiveScopes) {

            final List<Event> receivedEvents = new ArrayList<Event>();
            receivedEventsByScope.put(scope, receivedEvents);
            final SpreadWrapper inWrapper = Utilities.createSpreadWrapper();
            final SpreadInPushConnector inPort = new SpreadInPushConnector(
                    inWrapper, this.getConverterStrategy("utf-8-string"));
            inPort.addHandler(new EventHandler() {

                @Override
                public void handle(final Event e) {
                    synchronized (receivedEventsByScope) {
                        receivedEvents.add(e);
                        receivedEventsByScope.notify();
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

        this.outPort.push(event);

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
        for (final SpreadInPushConnector inPort : inPorts) {
            inPort.deactivate();
        }

    }

    @Test
    public void longGroupNames() throws Throwable {

        final Event event = new Event(String.class, "a test string");
        event.setScope(OUT_BASE_SCOPE
                .concat(new Scope(
                        "/is/a/very/long/scope/that/would/never/fit/in/a/spread/group/directly")));
        event.setId(new ParticipantId(), 452334);

        this.outPort.push(event);

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
        final SpreadWrapper inWrapper = Utilities.createSpreadWrapper();
        final SpreadInPushConnector inPort = new SpreadInPushConnector(
                inWrapper, this.getConverterStrategy("utf-8-string"));
        inPort.addHandler(new EventHandler() {

            @Override
            public void handle(final Event e) {
                synchronized (receivedEvents) {
                    receivedEvents.add(e);
                    receivedEvents.notify();
                }
            }

        });
        inPort.setScope(scope);
        inPort.activate();

        Thread.sleep(500);

        // send event
        final long beforeSend = System.currentTimeMillis() * 1000;
        this.outPort.push(event);
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

}
