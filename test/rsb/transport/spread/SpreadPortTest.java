/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
package rsb.transport.spread;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import rsb.Event;
import rsb.Scope;
import rsb.event.EventId;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.transport.EventHandler;
import rsb.transport.convert.ByteBufferConverter;

/**
 * @author jwienke
 */
public class SpreadPortTest {

	@Test(timeout = 10000)
	public void hierarchicalSending() throws Throwable {

		SpreadWrapper outWrapper = new SpreadWrapper();
		SpreadPort outPort = new SpreadPort(outWrapper, null);
		outPort.addConverter("string", new ByteBufferConverter());
		outPort.activate();

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

			});
			inPort.addConverter("string", new ByteBufferConverter());

			inPort.activate();

			inPort.notify(new ScopeFilter(scope), FilterAction.ADD);

			inPorts.add(inPort);

		}

		int numEvents = 100;

		// send events
		Event event = new Event("string");
		event.setId(new EventId());
		event.setData("a test string " + numEvents);
		event.setScope(sendScope);

		outPort.push(event);

		// wait for all receivers to get the scope
		for (Scope scope : receiveScopes) {
			synchronized (receivedEventsByScope) {
				while (receivedEventsByScope.get(scope).size() != 1) {
					receivedEventsByScope.wait();
				}
				assertEquals(event, receivedEventsByScope.get(scope).get(0));
			}
		}

		// deactivate ports
		for (SpreadPort inPort : inPorts) {
			inPort.deactivate();
		}
		outPort.deactivate();

	}

	@Test
	public void longGroupNames() throws Throwable {

		SpreadWrapper outWrapper = new SpreadWrapper();
		SpreadPort outPort = new SpreadPort(outWrapper, null);
		outPort.addConverter("string", new ByteBufferConverter());
		outPort.activate();

		Event event = new Event("string");
		event.setId(new EventId());
		event.setData("a test string");
		event.setScope(new Scope(
				"/this/is/a/very/long/scope/that/would/never/fit/in/a/spread/group/directly"));

		outPort.push(event);

		outPort.deactivate();

	}

}
