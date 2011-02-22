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
package rsb;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Publisher.PublisherStateActive;
import rsb.Publisher.PublisherStateInactive;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 *
 */
public class PublisherTest {

	/**
	 * Test method for {@link rsb.Publisher#Publisher(java.lang.String)}.
	 */
	@Test
	public void testPublisherString() {
		Publisher p = new Publisher("rsb://informer/example");
		assertNotNull(p);
		assertEquals(p.getURI(), "rsb://informer/example");
	}

	/**
	 * Test method for {@link rsb.Publisher#Publisher(java.lang.String, rsb.transport.TransportFactory)}.
	 */
	@Test
	public void testPublisherStringTransportFactory() {
		Publisher p = new Publisher("x",TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getURI(), "x");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for {@link rsb.Publisher#getURI()}.
	 */
	@Test
	public void testGetURI() {
		Publisher p = new Publisher("rsb://informer/example");
		assertEquals(p.getURI(), "rsb://informer/example");
	}

	/**
	 * Test method for {@link rsb.Publisher#activate()}.
	 */
	@Test
	public void testActivate() {
		Publisher p = new Publisher("rsb://informer/example");
		try {
			p.activate();
		} catch (InitializeException e) {
			e.printStackTrace();
			fail("Initialization exception!");
		}
		assertTrue(p.state instanceof PublisherStateActive);
	}

	/**
	 * Test method for {@link rsb.Publisher#deactivate()}.
	 */
	@Test
	public void testDeactivate() {
		Publisher p = new Publisher("rsb://informer/example");
		try {
			p.activate();
		} catch (InitializeException e) {
			e.printStackTrace();
			fail("Initialization exception!");
		}
		assertTrue(p.state instanceof PublisherStateActive);		
		p.deactivate();
		assertTrue(p.state instanceof PublisherStateInactive);
	}

	/**
	 * Test method for {@link rsb.Publisher#send(rsb.RSBEvent)}.
	 */
	@Test
	public void testSend() {
		Publisher p = new Publisher("rsb://informer/example");
		try {
			p.activate();
		} catch (InitializeException e) {
			e.printStackTrace();
			fail("Initialization exception!");
		}
		p.send(new RSBEvent("string","Hello World!"));
	}

}
