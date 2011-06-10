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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Informer.InformerStateActive;
import rsb.Informer.InformerStateInactive;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 */
public class InformerTest {

	private final Scope defaultScope = new Scope("/informer/example");
	private Informer<String> informer;

	@Before
	public void setUp() throws Throwable {
		informer = new Informer<String>(defaultScope);
		informer.activate();
	}

	@After
	public void tearDown() {
		if (informer.isActive()) {
			informer.deactivate();
		}
	}

	/**
	 * Test method for {@link rsb.Informer#Informer(java.lang.String)}.
	 */
	@Test
	public void testInformerString() {
		assertNotNull(informer);
		assertEquals(informer.getScope(), defaultScope);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testInformerStringTransportFactory() {
		final Scope scope = new Scope("/x");
		Informer<String> p = new Informer<String>(scope,
				TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), scope);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInformerStringString() {
		final Scope scope = new Scope("/x");
		final String type = "XMLString";
		Informer<String> p = new Informer<String>(scope, type);
		assertNotNull(p);
		assertEquals(p.getScope(), scope);
		assertEquals(p.typeinfo, type);
	}

	/**
	 * Test method for
	 * {@link rsb.Informer#Informer(java.lang.String, java.lang.String, rsb.transport.TransportFactory)}
	 * .
	 */
	@Test
	public void testInformerStringStringTransportFactory() {
		final Scope scope = new Scope("/x");
		final String type = "XMLString";
		Informer<String> p = new Informer<String>(scope, type,
				TransportFactory.getInstance());
		assertNotNull(p);
		assertEquals(p.getScope(), scope);
		assertEquals(p.typeinfo, type);
	}

	/**
	 * Test method for {@link rsb.Informer#getScope()}.
	 */
	@Test
	public void testGetScope() {
		assertEquals(informer.getScope(), defaultScope);
	}

	/**
	 * Test method for {@link rsb.Informer#activate()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testActivate() throws Throwable {
		assertTrue(informer.state instanceof InformerStateActive);
	}

	/**
	 * Test method for {@link rsb.Informer#deactivate()}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		assertTrue(informer.state instanceof InformerStateActive);
		informer.deactivate();
		assertTrue(informer.state instanceof InformerStateInactive);
	}

	private void testEvent(Event e, Participant participant) {
		assertEquals("string", e.getType());
		assertEquals("Hello World!", e.getData());
		assertNotNull(e.getId());
		assertEquals(new Scope("/informer/example"), e.getScope());
		assertEquals(participant.getId(), e.getMetaData().getSenderId());
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSendEvent() throws Throwable {
		Event e = informer.send(new Event(defaultScope, "string",
				"Hello World!"));
		testEvent(e, informer);
	}

	/**
	 * Test method for {@link rsb.Informer#send(rsb.Event)}.
	 * 
	 * @throws InitializeException
	 */
	@Test
	public void testSendT() throws Throwable {
		Event e = informer.send("Hello World!");
		testEvent(e, informer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventNullScope() throws Throwable {
		Event e = new Event();
		e.setType(informer.getTypeInfo());
		e.setScope(null);
		e.setData("foo");
		informer.send(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventWrongScope() throws Throwable {
		Event e = new Event();
		e.setType(informer.getTypeInfo());
		e.setScope(defaultScope.concat(new Scope("/blubb")));
		e.setData("foo");
		informer.send(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventNullType() throws Throwable {
		Event e = new Event();
		e.setType(null);
		e.setScope(defaultScope);
		e.setData("foo");
		informer.send(e);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSendEventWrongType() throws Throwable {
		Event e = new Event();
		e.setType("wrong");
		e.setScope(defaultScope);
		e.setData("foo");
		informer.send(e);
	}
}
