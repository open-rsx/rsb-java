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
import rsb.Scope;

/**
 * @author swrede
 *
 */
public class ServerTest {

	/**
	 * Test method for {@link rsb.patterns.Server#Server(rsb.Scope, rsb.transport.TransportFactory, rsb.transport.PortConfiguration)}.
	 */
	@Test
	public void testServer() {
		Factory factory = Factory.getInstance();
		Server server = factory.createLocalServer(new Scope("/example/server"));
		assertNotNull(server);
	}

//	/**
//	 * Test method for {@link rsb.patterns.Server#getMethods()}.
//	 */
//	@Test
//	public void testGetMethods() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link rsb.patterns.Server#isActive()}.
//	 */
//	@Test
//	public void testIsActive() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link rsb.patterns.Server#activate()}.
//	 */
//	@Test
//	public void testActivate() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link rsb.patterns.Server#deactivate()}.
//	 */
//	@Test
//	public void testDeactivate() {
//		fail("Not yet implemented");
//	}

}
