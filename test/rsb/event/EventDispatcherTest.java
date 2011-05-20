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
package rsb.event;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.Event;

/**
 * @author swrede
 * 
 */
public class EventDispatcherTest {

	private final class TestListener extends EventListener<Event> {
		boolean notified = false;

		public boolean isNotified() {
			return notified;
		}

		@Override
		public void handleEvent(Event e) {
			notified = true;
		}
	}

	/**
	 * Test method for {@link rsb.event.EventProcessor#EventDispatcher()}.
	 */
	@Test
	public final void testEventDispatcher() {
		EventProcessor ed = new EventProcessor();
		assertNotNull(ed);
		// TODO test configuration
	}

	/**
	 * Test method for
	 * {@link rsb.event.EventProcessor#EventDispatcher(int, int, int)}.
	 */
	@Test
	public final void testEventDispatcherIntIntInt() {
		EventProcessor ed = new EventProcessor(1, 3, 100);
		assertNotNull(ed);
	}

	/**
	 * Test method for
	 * {@link rsb.event.EventProcessor#addSubscription(rsb.event.Subscription)}.
	 */
	@Test
	public final void testAddSubscription() {
		EventProcessor ed = new EventProcessor();
		Subscription s = new Subscription();
		ed.addSubscription(s);
		assertTrue(ed.subscriptions.contains(s));
	}

	/**
	 * Test method for
	 * {@link rsb.event.EventProcessor#removeSubscription(rsb.event.Subscription)}
	 * .
	 */
	@Test
	public final void testRemoveSubscription() {
		EventProcessor ed = new EventProcessor();
		Subscription s = new Subscription();
		ed.addSubscription(s);
		ed.removeSubscription(s);
		assertTrue(ed.subscriptions.size() == 0);
	}

	private EventListener<Event> getHandler() {
		EventListener<Event> l = new TestListener();
		return l;
	}

	/**
	 * Test method for {@link rsb.event.EventProcessor#fire(rsb.Event)}.
	 * @throws InterruptedException 
	 */
	@Test
	public final void testFire() throws InterruptedException {
		EventProcessor ed = new EventProcessor();
		Subscription s = new Subscription();
		TestListener l = (TestListener) getHandler();
		s.appendHandler(l);
		ed.addSubscription(s);
		ed.fire(new Event());
		ed.waitForShutdown();
		assertTrue(l.isNotified());
	}

}
