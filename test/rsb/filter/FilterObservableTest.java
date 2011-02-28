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

/**
 * @author swrede
 *
 */
public class FilterObservableTest {

	private final class TestObserver implements FilterObserver {
		boolean notified_id = false;
		boolean notified_tf = false;
		boolean notified_sf = false;
		boolean notified_af = false;

		@Override
		public void notify(IdentityFilter e, FilterAction a) {
			notified_id = true;			
		}

		@Override
		public void notify(TypeFilter e, FilterAction a) {
			notified_tf = true;			
		}

		@Override
		public void notify(ScopeFilter e, FilterAction a) {
			notified_sf = true;			
		}

		@Override
		public void notify(AbstractFilter e, FilterAction a) {
			notified_af=true;			
		}
	}

	/**
	 * Test method for {@link rsb.filter.FilterObservable#addObserver(rsb.filter.FilterObserver)}.
	 */
	@Test
	public void testAddObserver() {
		FilterObservable fo = new FilterObservable();
		fo.addObserver(new TestObserver());
		assertTrue(fo.observers.size()==1);		
	}

	/**
	 * Test method for {@link rsb.filter.FilterObservable#removeObserver(rsb.filter.FilterObserver)}.
	 */
	@Test
	public void testRemoveObserver() {
		FilterObservable fo = new FilterObservable();
		TestObserver to = new TestObserver();
		fo.addObserver(to);
		fo.removeObserver(to);
		assertTrue(fo.observers.size()==0);	
	}

	/**
	 * Test method for {@link rsb.filter.FilterObservable#notifyObservers(rsb.filter.AbstractFilter, rsb.filter.FilterAction)}.
	 */
	@Test
	public void testNotifyObservers() {
		FilterObservable fo = new FilterObservable();
		TestObserver to = new TestObserver();
		fo.addObserver(to);
		fo.notifyObservers(new ScopeFilter("blub"), FilterAction.ADD);
		fo.notifyObservers(new IdentityFilter("blub", IdentityFilter.Type.RECEIVER_IDENTITY), FilterAction.ADD);
		fo.notifyObservers(new TypeFilter(this.getClass()), FilterAction.ADD);
		assertTrue(to.notified_sf);
		assertTrue(to.notified_tf);
		assertTrue(to.notified_id);
		assertFalse(to.notified_af);
	}

	/**
	 * Test method for {@link rsb.filter.FilterObservable#clearObservers()}.
	 */
	@Test
	public void testClearObservers() {
		FilterObservable fo = new FilterObservable();
		TestObserver to = new TestObserver();
		fo.addObserver(to);
		fo.clearObservers();
		assertTrue(fo.observers.size()==0);	
	}

}
