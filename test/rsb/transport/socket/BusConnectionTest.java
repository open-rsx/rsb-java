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
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.junit.Test;

import rsb.Event;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;

/**
 * @author swrede
 *
 */
public class BusConnectionTest {

	static final int HANDSHAKE        = 0x00000000;

	@Test
	public void testClientConnection() {
		try {
			// prototyping code that works with rsb_listener example
			
			// instantiate Socket object
			Socket socket = new Socket("localhost",55555);

			// get i/o streams 
			ReadableByteChannel rbc = Channels.newChannel(socket.getInputStream());
			WritableByteChannel wbc = Channels.newChannel(socket.getOutputStream());

			// do RSB socket transport handshake
			connect(rbc, wbc);
	
			// process packet
			int i = 0;
			while (socket.isConnected()) {
				i++;
				System.out.println("Waiting for Notification #" + i);
				Notification n = processNotification(rbc);
				// convert to Event
				Event e = EventBuilder.fromNotification(n);

				System.out.println("Scope: " + e.getScope());
				System.out.println("Id: " + e.getId());
				System.out.println("------------------------------");				
				if (i==1200) break;
			}
			
			// cleanup
			rbc.close(); 
			wbc.close(); 
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// cleanup
		}
		
	
	}

	private Notification processNotification(ReadableByteChannel rbc) throws IOException {
		// get size
		int length = readLength(rbc);
		System.out.println("Length of payload: " + length);
		
		// get notification
		Notification n = readNotification(rbc,length);
		return n;
	}

	/**
	 * @param rbc
	 * @param wbc
	 * @return
	 * @throws IOException
	 */
	protected void connect(ReadableByteChannel rbc,
			WritableByteChannel wbc) throws IOException {
		ByteBuffer buf_handshake = ByteBuffer.allocateDirect(4);
		buf_handshake.asIntBuffer().put(HANDSHAKE);
					
		System.out.print("Request:" + buf_handshake.getInt());
		wbc.write(buf_handshake); // write(HANDSHAKE);
		
		// read
		ByteBuffer bb = ByteBuffer.allocateDirect(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);	
		System.out.println("Bytes read: " + rbc.read(bb));
		
		// check if reply = 0x00000000;
		bb.rewind();
		if (HANDSHAKE==bb.getInt()) {
			System.out.print("Handshake successfull!");
		}
	}

	/**
	 * @param rbc
	 * @param bb
	 * @return
	 * @throws IOException
	 */
	protected int readLength(ReadableByteChannel rbc)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println("Bytes read: " + rbc.read(bb));
		bb.rewind();
		return bb.getInt();
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

	private Notification readNotification(ReadableByteChannel rbc, int length) {
		byte[] buf = new byte[length];
		ByteBuffer buf_notification = ByteBuffer.wrap(buf);
		// Why does the following not work?!?
		// ByteBuffer buf_notification = ByteBuffer.allocateDirect(length);
		buf_notification.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println("Bytes to be read: " + buf_notification.remaining());
		Notification n = null;
		try {
			System.out.println("Bytes read: " + rbc.read(buf_notification));
			saveRawNotification(buf_notification);
			buf_notification.rewind();
			n = Notification.parseFrom(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}

}
