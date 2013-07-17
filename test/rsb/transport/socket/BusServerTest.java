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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;

/**
 * @author swrede
 */
public class BusServerTest {

    @Test
    public void busServer() throws IOException {
        final InetAddress addr = InetAddress.getLocalHost();
        final BusServer server = new BusServer(addr, 55555);
        server.activate();
        assertNotNull(server);
        server.deactivate();
    }

    @Test
    public void busServerRun() throws IOException, InterruptedException {
        final InetAddress addr = InetAddress.getLocalHost();
        final BusServer server = new BusServer(addr, 55555);
        server.activate();
        final Thread serverThread = new Thread(server);
        serverThread.start();
        Thread.sleep(1000);
        assertNotNull(server);
        server.deactivate();
    }

}
