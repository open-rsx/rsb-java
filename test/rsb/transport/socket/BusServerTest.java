/**
 * ============================================================
 *
 * This file is a part of the rsb-java project
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

import java.io.IOException;
import java.net.InetAddress;
import org.junit.Test;

/**
 * @author swrede
 *
 */
public class BusServerTest {

	/**
	 * Test method for {@link rsb.transport.socket.BusServer#BusServer(java.net.InetAddress, int)}.
	 * @throws IOException 
	 */
	@Test
	public void testBusServer() throws IOException {
		InetAddress addr = InetAddress.getLocalHost();
		BusServer server = new BusServer(addr, 55555);
		server.activate();
		assertNotNull(server);
		server.deactivate();
	}

	@Test
	public void testBusServerRun() throws IOException, InterruptedException {
		InetAddress addr = InetAddress.getLocalHost();
		BusServer server = new BusServer(addr, 55555);
		server.activate();
		Thread serverThread = new Thread(server);
		serverThread.start();
		Thread.sleep(1000);
		assertNotNull(server);
		server.deactivate();
	}	
	
}
