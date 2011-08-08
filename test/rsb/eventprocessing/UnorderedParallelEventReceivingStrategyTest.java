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
import rsb.AbstractEventHandler;
import rsb.eventprocessing.UnorderedParallelEventReceivingStrategy;

/**
 * @author swrede
 */
public class UnorderedParallelEventReceivingStrategyTest {

	private final class TestHandler extends AbstractEventHandler {
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
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		assertNotNull(ed);
		// TODO test configuration
	}

	@Test
	public final void testEventDispatcherIntIntInt() {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy(
				1, 3, 100);
		assertNotNull(ed);
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#addHandler(rsb.event.Handler)}
	 * .
	 */
	@Test
	public final void testAddHandler() {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler h = new TestHandler();
		ed.addHandler(h, true);
		assertTrue(ed.getHandlers().contains(h));
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#removeHandler(rsb.event.Handler)}
	 * .
	 */
	@Test
	public final void testRemoveSubscription() throws InterruptedException {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler h = new TestHandler();
		ed.addHandler(h, true);
		ed.removeHandler(h, true);
		assertTrue(ed.getHandlers().size() == 0);
	}

	private AbstractEventHandler getHandler() {
		return new TestHandler();
	}

	/**
	 * Test method for
	 * {@link rsb.eventprocessing.UnorderedParallelEventReceivingStrategy#fire(rsb.Event)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public final void testFire() throws InterruptedException {
		UnorderedParallelEventReceivingStrategy ed = new UnorderedParallelEventReceivingStrategy();
		TestHandler l = (TestHandler) getHandler();
		ed.addHandler(l, true);
		long beforeFire = System.currentTimeMillis() * 1000;
		Event event = new Event();
		ed.handle(event);
		ed.shutdownAndWait();
		long afterShutdown = System.currentTimeMillis() * 1000;
		assertTrue(l.isNotified());
		assertSame(event, l.event);
		assertTrue(event.getMetaData().getDeliverTime() >= beforeFire);
		assertTrue(event.getMetaData().getDeliverTime() <= afterShutdown);
	}
}
