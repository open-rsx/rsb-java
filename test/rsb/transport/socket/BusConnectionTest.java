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

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import rsb.Event;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;

/**
 * @author swrede
 *
 */
public class BusConnectionTest {

	@Test
	public void testBusConnection() throws UnknownHostException {
//		def __init__(self,
//				  65                   host = None, port = None, socket_ = None,
//				  66                   isServer = False):
		InetAddress addr = InetAddress.getLocalHost();
		BusConnection bus1 = new BusConnection(addr,55555,false);
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();	
	
	//@Test
	// TODO add Server mock for testing
	public void testBusConnectionDeActivation() throws IOException, RSBException {
		// instantiate Socket object
		// precondition: server is running!!!
		Socket socket1 = new Socket(InetAddress.getLocalHost(),55555);
		Socket socket2 = new Socket(InetAddress.getLocalHost(),55555);
		InetAddress addr = InetAddress.getLocalHost();
		BusConnection bus1 = new BusConnection(addr,55555);	
	    bus1.activate();
		bus1.deactivate();
		bus1.activate();
		bus1.deactivate();
		bus1.activate();
	}
	
	@Test
	public void testClientConnection() throws RSBException {
		try {
			// prototyping code that works with rsb_listener example
			InetAddress addr = InetAddress.getLocalHost();
			BusConnection bus = new BusConnection(addr,55555);	
		    bus.activate();
	
			// process packet
			int i = 0;
			while (true) {
				i++;
				System.out.println("Waiting for Notification #" + i);
				Notification n = bus.readNotification();
				// convert to Event
				Event e = EventBuilder.fromNotification(n);

				System.out.println("Scope: " + e.getScope());
				System.out.println("Id: " + e.getId());
				System.out.println("------------------------------");				
				if (i==1200) break;
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// cleanup
		}
		
	
	}




	private void saveRawNotification(ByteBuffer buf_notificaton) {
//		System.out.println("Extra bytes read: " + rbc.read(buf_notification));
		
//		File file = new File("pbuf.data");
//
//		// Set to true if the bytes should be appended to the file;
//		// set to false if the bytes should replace current bytes
//		// (if the file exists)
//		boolean append = false;
//
//		try {
//		    // Create a writable file channel
//		    FileChannel wChannel = new FileOutputStream(file, append).getChannel();
//
//		    // Write the ByteBuffer contents; the bytes between the ByteBuffer's
//		    // position and the limit is written to the file
//		    wChannel.write(buf_notification);
//
//		    // Close the file
//		    wChannel.close();
//		} catch (IOException e) {
//		}	
		
	}



}
