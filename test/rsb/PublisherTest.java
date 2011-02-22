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
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
		assertNotNull(p);
		assertEquals(p.getURI(), "rsb://informer/example");
	}

	/**
	 * Test method for {@link rsb.Publisher#Publisher(java.lang.String, rsb.transport.TransportFactory)}.
	 */
	@Test
	public void testPublisherStringTransportFactory() {
		Publisher<String> p = new Publisher<String>("x",TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getURI(), "x");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for {@link rsb.Publisher#Publisher(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPublisherStringString() {
		Publisher<String> p = new Publisher<String>("x","XMLString");
		assertNotNull(p);
		assertEquals(p.getURI(), "x");
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}		
	
	/**
	 * Test method for {@link rsb.Publisher#Publisher(java.lang.String, java.lang.String, rsb.transport.TransportFactory)}.
	 */
	@Test
	public void testPublisherStringStringTransportFactory() {
		Publisher<String> p = new Publisher<String>("x","XMLString",TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getURI(), "x");
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}	
	
	/**
	 * Test method for {@link rsb.Publisher#getURI()}.
	 */
	@Test
	public void testGetURI() {
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
		assertEquals(p.getURI(), "rsb://informer/example");
	}

	/**
	 * Test method for {@link rsb.Publisher#activate()}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testActivate() {
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
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
	@SuppressWarnings("unchecked")
	@Test
	public void testDeactivate() {
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
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

	private void testEvent(RSBEvent e) {
		assertTrue(e.type.equals("string"));
		assertTrue(e.data.equals("Hello World!"));
		assertNotNull(e.getUuid());
		assertTrue(e.getUri().equals("rsb://informer/example"));		
	}	
	
	/**
	 * Test method for {@link rsb.Publisher#send(rsb.RSBEvent)}.
	 */
	@Test
	public void testSendRSBEvent() {
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
		try {
			p.activate();
		} catch (InitializeException e) {
			e.printStackTrace();
			fail("Initialization exception!");
		}
		RSBEvent e = p.send(new RSBEvent("string","Hello World!"));
		testEvent(e);
	}
	

	/**
	 * Test method for {@link rsb.Publisher#send(rsb.RSBEvent)}.
	 */
	@Test
	public void testSendT() {
		Publisher<String> p = new Publisher<String>("rsb://informer/example");
		try {
			p.activate();
		} catch (InitializeException e) {
			e.printStackTrace();
			fail("Initialization exception!");
		}
		RSBEvent e = p.send("Hello World!");
		testEvent(e);
	}	
	
	// TODO add testcase for unknown data type 

}
