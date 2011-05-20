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
package rsb.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Event;
import rsb.Scope;
import rsb.event.EventListener;
import rsb.event.Subscription;

/**
 * @author swrede
 * 
 */
public class SubscriptionTest {

	private EventListener<Event> getHandler() {
		EventListener<Event> l = new EventListener<Event>() {

			@Override
			public void handleEvent(Event e) {
				// blub
			}

		};
		return l;
	}

	/**
	 * Test method for
	 * {@link rsb.event.Subscription#appendHandler(rsb.event.EventListener)}.
	 */
	@Test
	public void testAppendHandler() {
		EventListener<Event> el = getHandler();
		Subscription s = new Subscription();
		s.appendHandler(el);
		assertTrue(s.getHandlers().contains(el));
	}

	/**
	 * Test method for
	 * {@link rsb.event.Subscription#appendFilter(rsb.filter.Filter)}.
	 */
	@Test
	public void testAppendFilter() {
		ScopeFilter sf = new ScopeFilter(new Scope("/blub"));
		Subscription s = new Subscription();
		s.appendFilter(sf);
		assertTrue(s.getFilter().contains(sf));
	}

	/**
	 * Test method for {@link rsb.event.Subscription#getHandlerIterator()}.
	 */
	@Test
	public void testGetHandlerIterator() {
		EventListener<Event> el = getHandler();
		Subscription s = new Subscription();
		s.appendHandler(el);
		assertTrue(s.getHandlerIterator().next().equals(el));
	}

	/**
	 * Test method for {@link rsb.event.Subscription#getFilterIterator()}.
	 */
	@Test
	public void testGetFilterIterator() {
		ScopeFilter sf = new ScopeFilter(new Scope("/blub"));
		Subscription s = new Subscription();
		s.appendFilter(sf);
		assertTrue(s.getFilterIterator().next().equals(sf));
	}

	/**
	 * Test method for {@link rsb.event.Subscription#length()}.
	 */
	@Test
	public void testLength() {
		ScopeFilter sf = new ScopeFilter(new Scope("/blub"));
		Subscription s = new Subscription();
		s.appendFilter(sf);
		assertTrue(s.length() == 1);
	}

	/**
	 * Test method for {@link rsb.event.Subscription#match(rsb.Event)}.
	 */
	@Test
	public void testMatch() {
		ScopeFilter sf = new ScopeFilter(new Scope("/blub"));
		Subscription s = new Subscription();
		s.appendFilter(sf);
		Event e = new Event();
		e.ensureId();
		e.setScope(new Scope("/blub"));
		assertTrue(s.match(e));
	}

}
