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

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rsb.RSBEvent;
import rsb.filter.Subscription;

/**
 * @author swrede
 *
 */
public class EventDispatcherTest {

	private final class TestListener implements RSBEventListener<RSBEvent> {
		boolean notified = false;

		public boolean isNotified() {
			return notified;
		}

		@Override
		public void handleEvent(RSBEvent e) {				
			notified = true;
		}
	}

	/**
	 * Test method for {@link rsb.event.EventDispatcher#EventDispatcher()}.
	 */
	@Test
	public final void testEventDispatcher() {
		EventDispatcher ed = new EventDispatcher();
		assertNotNull(ed);
		// TODO test configuration
	}

	/**
	 * Test method for {@link rsb.event.EventDispatcher#EventDispatcher(int, int, int)}.
	 */
	@Test
	public final void testEventDispatcherIntIntInt() {
		EventDispatcher ed = new EventDispatcher(1,3, 100);
		assertNotNull(ed);		
	}

	/**
	 * Test method for {@link rsb.event.EventDispatcher#addSubscription(rsb.filter.Subscription)}.
	 */
	@Test
	public final void testAddSubscription() {
		EventDispatcher ed = new EventDispatcher();
		Subscription s = new Subscription();
		ed.addSubscription(s);
		assertTrue(ed.subscriptions.contains(s));
	}

	/**
	 * Test method for {@link rsb.event.EventDispatcher#removeSubscription(rsb.filter.Subscription)}.
	 */
	@Test
	public final void testRemoveSubscription() {
		EventDispatcher ed = new EventDispatcher();
		Subscription s = new Subscription();
		ed.addSubscription(s);
		ed.removeSubscription(s);
		assertTrue(ed.subscriptions.size()==0);
	}

	private RSBEventListener<RSBEvent> getHandler() {
		RSBEventListener<RSBEvent> l = new TestListener();
		return l;
	}	
	
	/**
	 * Test method for {@link rsb.event.EventDispatcher#fire(rsb.RSBEvent)}.
	 */
	@Test
	public final void testFire() {
		EventDispatcher ed = new EventDispatcher();
		Subscription s = new Subscription();
		TestListener l = (TestListener) getHandler();
		s.appendHandler(l);
		ed.addSubscription(s);
		ed.fire(new RSBEvent());		
		ed.waitForShutdown();
		assertTrue(l.isNotified());
	}

}
