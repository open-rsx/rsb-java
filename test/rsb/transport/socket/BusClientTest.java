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

import rsb.RSBException;

/**
 * @author swrede
 */
public class BusClientTest {

    @Test
    public void busClient() throws IOException, RSBException {
        final InetAddress addr = InetAddress.getLocalHost();
        final BusClient client = new BusClient(addr, 55555);
        // client.activate();
        assertNotNull(client);
        client.deactivate();
    }

    @Test
    public void handleIncoming() throws Throwable {
        final InetAddress addr = InetAddress.getLocalHost();
        final BusClient client = new BusClient(addr, 55555);
        // TODO add here some automated setup of the server side once it is
        // implemtend in Java
        // client.activate()
        assertNotNull(client);
        // Thread.sleep(100);
        client.deactivate();
    }

}
