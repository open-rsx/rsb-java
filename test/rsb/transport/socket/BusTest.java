/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
package rsb.transport.socket;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import rsb.protocol.NotificationType.Notification;

/**
 * @author swrede
 *
 */
public class BusTest {

	/**
	 * Test method for {@link rsb.transport.socket.Bus#handleOutgoing(rsb.protocol.NotificationType.Notification)}.
	 * @throws UnknownHostException 
	 */
	@Test
	public void testHandleOutgoing() throws UnknownHostException {
		Bus bus = new Bus();
		BusConnection con = new BusConnection(InetAddress.getLocalHost(), 55555);
		bus.addConnection(con);
		bus.addConnection(con);
		assertTrue(bus.connections.size()==2);
		Notification n = Notification.getDefaultInstance();
		bus.handleOutgoing(n);
		bus.removeConnection(con);
		assertTrue(bus.connections.size()==1);
	}

	/**
	 * Test method for {@link rsb.transport.socket.Bus#addConnection(rsb.transport.socket.BusConnection)}.
	 * @throws UnknownHostException 
	 */
	@Test
	public void testAddandRemoveConnection() throws UnknownHostException {
		Bus bus = new Bus();
		assertTrue(bus.connections.isEmpty());
		BusConnection con = new BusConnection(InetAddress.getLocalHost(), 55555);
		bus.addConnection(con);
		assertTrue(bus.connections.size()==1);
		bus.removeConnection(con);
		assertTrue(bus.connections.isEmpty());
	}

}
