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
import rsb.ParticipantId;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ScopeFilterTest {

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#transform(rsb.Event)}.
	 */
	@Test
	public void testTransform() {
		Event e = new Event();
		e.setScope(new Scope("/images"));
		e.setId(new ParticipantId(), 234);
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertTrue(sf.transform(e) != null);
		e.setScope(new Scope("/nomatch"));
		assertTrue(sf.transform(e) == null);
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#skip(rsb.event.EventId)}.
	 */
	@Test
	public void testSkipEventId() {
		Event e = new Event();
		// TODO actually, we need a mock object to test this correctly
		// setting the Scope to another name than the scope filters
		// configuration is just to check here whether the white-
		// listing really works
		e.setScope(new Scope("/images/justfortesting"));
		e.setId(new ParticipantId(), 43543);
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		sf.skip(e.getId());
		assertTrue(sf.transform(e) != null);
	}

	/**
	 * Test method for
	 * {@link rsb.filter.ScopeFilter#ScopeFilter(java.lang.String)}.
	 */
	@Test
	public void testScopeFilter() {
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertNotNull(sf);
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#getScope()}.
	 */
	@Test
	public void testGetScope() {
		ScopeFilter sf = new ScopeFilter(new Scope("/images"));
		assertTrue(sf.getScope().equals(new Scope("/images")));
	}

	/**
	 * Test method for {@link rsb.filter.ScopeFilter#equals(rsb.filter.Filter)}.
	 */
	@Test
	public void testEqualsFilter() {
		ScopeFilter sf1 = new ScopeFilter(new Scope("/images"));
		ScopeFilter sf2 = new ScopeFilter(new Scope("/images"));
		assertTrue(sf1.equals(sf2));
		sf2.setScope(new Scope("/nope"));
		assertFalse(sf1.equals(sf2));
	}

}
