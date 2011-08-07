/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.patterns;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Factory;
import rsb.InitializeException;
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ServerTest {

	protected class ReplyCallback implements DataCallback<String, String> {
		@Override
		public String invoke(String request) throws Throwable {
			return request;
		}
	}
	
	/**
	 * Test method for {@link rsb.patterns.Server#Server(rsb.Scope, rsb.transport.TransportFactory, rsb.transport.PortConfiguration)}.
	 */
	@Test
	public void testServer() {
		Server server = getServer();
		assertNotNull(server);
	}

	private Server getServer() {
		Factory factory = Factory.getInstance();
		Server server = factory.createLocalServer(new Scope("/example/server"));
		return server;
	}

	/**
	 * Test method for {@link rsb.patterns.Server#getMethods()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testGetMethods() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		assertTrue(server.getMethods().size()==0);
		server.addMethod("callme", new ReplyCallback());
		assertTrue(server.getMethods().size()==1);
		assertTrue(server.getMethods().iterator().next().getName().equals("callme"));
	}

	@Test
	public void addMethod() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		server.addMethod("callme", new ReplyCallback());
	}
	
	/**
	 * Test method for {@link rsb.patterns.Server#activate()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testActivate() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		assertFalse(server.isActive());
		server.activate();
		assertTrue(server.isActive());
		server.deactivate();
		assertFalse(server.isActive());
		server.addMethod("callme", new ReplyCallback());
		server.activate();
	}

	/**
	 * Test method for {@link rsb.patterns.Server#deactivate()}.
	 * @throws InitializeException 
	 */
	@Test
	public void testDeactivate() throws InitializeException {
		LocalServer server = (LocalServer) getServer();
		DataCallback<String, String> method = new ReplyCallback();
		server.addMethod("callme", method);
		server.activate();
		assertTrue(server.isActive());
		assertTrue(server.getMethods().iterator().next().isActive());
		server.deactivate();
		assertFalse(server.isActive());
		assertFalse(server.getMethods().iterator().next().isActive());
	}

}
