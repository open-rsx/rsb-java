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

import rsb.Informer.InformerStateActive;
import rsb.Informer.InformerStateInactive;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 *
 */
public class InformerTest {

	/**
	 * Test method for {@link rsb.Informer#Informer(java.lang.String)}.
	 */
	@Test
	public void testInformerString() {
		Informer<String> p = new Informer<String>(new Scope(
				"/informer/example"));
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/informer/example"));
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testInformerStringTransportFactory() {
		Informer<String> p = new Informer<String>(new Scope("/x"),
				TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInformerStringString() {
		Informer<String> p = new Informer<String>(new Scope("/x"),
				"XMLString");
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testInformerStringStringTransportFactory() {
		Informer<String> p = new Informer<String>(new Scope("/x"),
				"XMLString", TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), new Scope("/x"));
		assertEquals(p.typeinfo, "XMLString");
		assertNotNull(p.transportFactory);
	}

	/**
	 * Test method for {@link rsb.Informer#getScope()}.
	 */
	@Test
	public void testGetScope() {
		Informer<String> p = new Informer<String>(new Scope(
				"/informer/example"));
		assertEquals(p.getScope(), new Scope("/informer/example"));
	}

	/**
	 * Test method for {@link rsb.Informer#activate()}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testActivate() throws Throwable {
		Informer<String> p = new Informer<String>(new Scope("/activate"));
		p.activate();
		assertTrue(p.state instanceof InformerStateActive);
	}

	/**
	 * Test method for {@link rsb.Informer#deactivate()}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		Informer<String> p = new Informer<String>(new Scope(
				"/informer/example"));
		p.activate();
		assertTrue(p.state instanceof InformerStateActive);
		p.deactivate();
		assertTrue(p.state instanceof InformerStateInactive);
	}

	private void testEvent(Event e) {
		assertTrue(e.getType().equals("string"));
		assertTrue(e.getData().equals("Hello World!"));
		assertNotNull(e.getId());
		assertTrue(e.getScope().equals(new Scope("/informer/example")));
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testSendEvent() throws InitializeException {
		Informer<String> p = new Informer<String>(new Scope(
				"/informer/example"));
		p.activate();
		Event e = p.send(new Event("string", "Hello World!"));
		testEvent(e);
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 *
	 * @throws InitializeException
	 */
	@Test
	public void testSendT() throws InitializeException {
		Informer<String> p = new Informer<String>(new Scope(
				"/informer/example"));
		p.activate();
		Event e = p.send("Hello World!");
		testEvent(e);
	}

	// TODO add testcase for unknown data type

}
