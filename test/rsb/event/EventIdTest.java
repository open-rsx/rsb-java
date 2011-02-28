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

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author swrede
 *
 */
public class EventIdTest {

	/**
	 * Test method for {@link rsb.event.EventId#EventId()}.
	 */
	@Test
	public void testEventId() {
		EventId id = EventId.generateId();
		assertNotNull(id.id);
	}

	/**
	 * Test method for {@link rsb.event.EventId#EventId(java.util.UUID)}.
	 */
	@Test
	public void testEventIdUUID() {
		UUID uuid = UUID.randomUUID();
		EventId id = new EventId(uuid);
		assertTrue(uuid.compareTo(id.id)==0);
	}

	/**
	 * Test method for {@link rsb.event.EventId#EventId(java.lang.String)}.
	 */
	@Test
	public void testEventIdString() {
		EventId id = EventId.generateId();
		String s = id.toString();
		EventId id2 = new EventId(s);
		assertTrue(id.equals(id2));
	}

	/**
	 * Test method for {@link rsb.event.EventId#toString()}.
	 */
	@Test
	public void testToString() {
		EventId id = EventId.generateId();
		String s = id.toString();
		assertTrue(s.equals("rsb:eid:"+id.id.toString()));		
	}

	/**
	 * Test method for {@link rsb.event.EventId#generateId()}.
	 */
	@Test
	public void testGenerateId() {
		EventId id = EventId.generateId();
		assertNotNull(id.id);
	}

	/**
	 * Test method for {@link rsb.event.EventId#get()}.
	 */
	@Test
	public void testGet() {
		EventId id = EventId.generateId();
		assertNotNull(id.get());		
	}

}
