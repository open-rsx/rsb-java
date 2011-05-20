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
		Publisher<String> p = new Publisher<String>(new Scope(
				"/informer/example"));
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/informer/example"));
	}

	/**
	 * Test method for
	 * {@link rsb.Publisher#Publisher(java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testPublisherStringTransportFactory() {
		Publisher<String> p = new Publisher<String>(new Scope("/x"),
				TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for
	 * {@link rsb.Publisher#Publisher(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPublisherStringString() {
		Publisher<String> p = new Publisher<String>(new Scope("/x"),
				"XMLString");
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for
	 * {@link rsb.Publisher#Publisher(java.lang.String, java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testPublisherStringStringTransportFactory() {
		Publisher<String> p = new Publisher<String>(new Scope("/x"),
				"XMLString", TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for {@link rsb.Publisher#getScope()}.
	 */
	@Test
	public void testGetScope() {
		Publisher<String> p = new Publisher<String>(new Scope(
				"/informer/example"));
		assertEquals(p.getScope(), new Scope("/informer/example"));
	}

	/**
	 * Test method for {@link rsb.Publisher#activate()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testActivate() throws Throwable {
		Publisher<String> p = new Publisher<String>(new Scope("/activate"));
		p.activate();
		assertTrue(p.state instanceof PublisherStateActive);
	}

	/**
	 * Test method for {@link rsb.Publisher#deactivate()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		Publisher<String> p = new Publisher<String>(new Scope(
				"/informer/example"));
		p.activate();
		assertTrue(p.state instanceof PublisherStateActive);
		p.deactivate();
		assertTrue(p.state instanceof PublisherStateInactive);
	}

	private void testEvent(Event e) {
		assertTrue(e.getType().equals("string"));
		assertTrue(e.getData().equals("Hello World!"));
		assertNotNull(e.getId());
		assertTrue(e.getScope().equals(new Scope("/informer/example")));
	}

	/**
	 * Test method for {@link rsb.Publisher#send(rsb.Event)}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSendRSBEvent() throws InitializeException {
		Publisher<String> p = new Publisher<String>(new Scope(
				"/informer/example"));
		p.activate();
		Event e = p.send(new Event("string", "Hello World!"));
		testEvent(e);
	}

	/**
	 * Test method for {@link rsb.Publisher#send(rsb.Event)}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSendT() throws InitializeException {
		Publisher<String> p = new Publisher<String>(new Scope(
				"/informer/example"));
		p.activate();
		Event e = p.send("Hello World!");
		testEvent(e);
	}

	// TODO add testcase for unknown data type

}
