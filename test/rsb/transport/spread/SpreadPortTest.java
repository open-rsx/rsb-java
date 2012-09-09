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

import static org.junit.Assert.*;

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
import rsb.converter.StringConverter;
import rsb.converter.UnambiguousConverterMap;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.transport.EventHandler;

/**
 * @author jwienke
 */
public class SpreadPortTest {

	private SpreadWrapper outWrapper;
	private SpreadPort outPort;

	@Before
	public void setUp() throws Throwable {

		outWrapper = new SpreadWrapper();
		outPort = new SpreadPort(outWrapper, null, getConverterStrategy("utf-8-string"),getConverterStrategy(String.class.getName()));
		outPort.setQualityOfServiceSpec(new QualityOfServiceSpec(
				Ordering.ORDERED, Reliability.RELIABLE));
		outPort.activate();

	}

	/**
	 * @return
	 */
	private UnambiguousConverterMap<ByteBuffer> getConverterStrategy(String key) {
		UnambiguousConverterMap<ByteBuffer> strategy = new UnambiguousConverterMap<ByteBuffer>();
		strategy.addConverter(key, new StringConverter());
		return strategy;
	}

	@After
	public void tearDown() throws Throwable {
		outPort.deactivate();
	}

	@Test(timeout = 10000)
	public void hierarchicalSending() throws Throwable {

		final Scope sendScope = new Scope("/this/is/a/hierarchy");

		List<Scope> receiveScopes = sendScope.superScopes(true);

		// install event handlers for all receive scopes
		final Map<Scope, List<Event>> receivedEventsByScope = new HashMap<Scope, List<Event>>();
		final List<SpreadPort> inPorts = new ArrayList<SpreadPort>();
		for (Scope scope : receiveScopes) {

			final List<Event> receivedEvents = new ArrayList<Event>();
			receivedEventsByScope.put(scope, receivedEvents);
			SpreadWrapper inWrapper = new SpreadWrapper();
			SpreadPort inPort = new SpreadPort(inWrapper, new EventHandler() {

				@Override
				public void handle(Event e) {
					synchronized (receivedEventsByScope) {
						receivedEvents.add(e);
						receivedEventsByScope.notify();
					}
				}

			},getConverterStrategy("utf-8-string"),getConverterStrategy(String.class.getName()));

			inPort.activate();

			inPort.notify(new ScopeFilter(scope), FilterAction.ADD);

			inPorts.add(inPort);

		}

		int numEvents = 100;

		// send events
		Event event = new Event(String.class);
		event.setData("a test string " + numEvents);
		event.setScope(sendScope);
		event.setId(new ParticipantId(), 42);

		outPort.push(event);

		// wait for all receivers to get the scope
		for (Scope scope : receiveScopes) {
			synchronized (receivedEventsByScope) {
				while (receivedEventsByScope.get(scope).size() != 1) {
					receivedEventsByScope.wait();
				}

				Event receivedEvent = receivedEventsByScope.get(scope).get(0);

				// normalize times as they are not important for this test
				event.getMetaData().setReceiveTime(
						receivedEvent.getMetaData().getReceiveTime());

				assertEquals(event, receivedEvent);
			}
		}

		// deactivate ports
		for (SpreadPort inPort : inPorts) {
			inPort.deactivate();
		}

	}

	@Test
	public void longGroupNames() throws Throwable {

		Event event = new Event(String.class);
		event.setData("a test string");
		event.setScope(new Scope(
				"/this/is/a/very/long/scope/that/would/never/fit/in/a/spread/group/directly"));
		event.setId(new ParticipantId(), 452334);

		outPort.push(event);

	}

	@Test(timeout = 10000)
	public void sendMetaData() throws Throwable {

		// create an event to send
		final Scope scope = new Scope("/a/test/scope/again");
		Event event = new Event(String.class);
		event.setData("a test string");
		event.setScope(scope);
		event.setId(new ParticipantId(), 634);

		// create a receiver to wait for event
		final List<Event> receivedEvents = new ArrayList<Event>();
		SpreadWrapper inWrapper = new SpreadWrapper();
		SpreadPort inPort = new SpreadPort(inWrapper, new EventHandler() {

			@Override
			public void handle(Event e) {
				synchronized (receivedEvents) {
					receivedEvents.add(e);
					receivedEvents.notify();
				}
			}

		}, getConverterStrategy("utf-8-string"),
				getConverterStrategy(String.class.getName()));
		inPort.activate();
		inPort.notify(new ScopeFilter(scope), FilterAction.ADD);

		Thread.sleep(500);

		// send event
		long beforeSend = System.currentTimeMillis() * 1000;
		outPort.push(event);
		long afterSend = System.currentTimeMillis() * 1000;

		assertTrue(event.getMetaData().getSendTime() >= beforeSend);
		assertTrue(event.getMetaData().getSendTime() <= afterSend);

		// wait for receiving the event
		synchronized (receivedEvents) {
			while (receivedEvents.size() != 1) {
				receivedEvents.wait();
			}

			Event receivedEvent = receivedEvents.get(0);

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
