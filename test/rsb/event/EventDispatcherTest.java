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

	private final class TestHandler extends EventHandler<Event> {
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
	 * {@link rsb.event.EventProcessor#addHandler(rsb.event.Handler)}.
	 */
	@Test
	public final void testAddHandler() {
		EventProcessor ed = new EventProcessor();
		TestHandler h = new TestHandler();
		ed.addHandler(h);
		assertTrue(ed.handlers.contains(h));
	}

	/**
	 * Test method for
	 * {@link rsb.event.EventProcessor#removeHandler(rsb.event.Handler)}
	 * .
	 */
	@Test
	public final void testRemoveSubscription() {
		EventProcessor ed = new EventProcessor();
		TestHandler h = new TestHandler();
		ed.addHandler(h);
		ed.removeHandler(h);
		assertTrue(ed.handlers.size() == 0);
	}

	private EventHandler<Event> getHandler() {
		EventHandler<Event> l = new TestHandler();
		return l;
	}

	/**
	 * Test method for {@link rsb.event.EventProcessor#fire(rsb.Event)}.
	 * @throws InterruptedException
	 */
	@Test
	public final void testFire() throws InterruptedException {
		EventProcessor ed = new EventProcessor();
		TestHandler l = (TestHandler) getHandler();
		ed.addHandler(l);
		ed.fire(new Event());
		ed.waitForShutdown();
		assertTrue(l.isNotified());
	}

}
