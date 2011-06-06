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
package rsb.eventprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Event;
import rsb.EventHandler;
import rsb.eventprocessing.EventProcessor;

/**
 * @author swrede
 * 
 */
public class EventProcessorTest {

	private final class TestHandler extends EventHandler {
		boolean notified = false;
		public Event event;

		public boolean isNotified() {
			return notified;
		}

		@Override
		public void handleEvent(Event e) {
			event = e;
			notified = true;
		}
	}

	@Test
	public final void testEventDispatcher() {
		EventProcessor ed = new EventProcessor();
		assertNotNull(ed);
		// TODO test configuration
	}

	@Test
	public final void testEventDispatcherIntIntInt() {
		EventProcessor ed = new EventProcessor(1, 3, 100);
		assertNotNull(ed);
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.EventProcessor#addHandler(rsb.event.Handler)}.
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
	 * {@link rsb.eventprocessing.EventProcessor#removeHandler(rsb.event.Handler)}
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

	private EventHandler getHandler() {
		return new TestHandler();
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.EventProcessor#fire(rsb.Event)}.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public final void testFire() throws InterruptedException {
		EventProcessor ed = new EventProcessor();
		TestHandler l = (TestHandler) getHandler();
		ed.addHandler(l);
		long beforeFire = System.nanoTime() / 1000;
		Event event = new Event();
		ed.fire(event);
		ed.waitForShutdown();
		long afterShutdown = System.nanoTime() / 1000;
		assertTrue(l.isNotified());
		assertSame(event, l.event);
		assertTrue(event.getMetaData().getDeliverTime() >= beforeFire);
		assertTrue(event.getMetaData().getDeliverTime() <= afterShutdown);
	}
}
