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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * Instances of this class implement connections to a socket-based bus.
 * 
 * The basic operations provided by this class are receiving an event notifications
 * by calling receiveNotification and submitting an event to the bus by
 * calling sendNotification. This class implements the fundamental RSB protocol
 * for socket connections, e.g., the basic handshaking and the encoding/decoding
 * of data packages for event notifications. 
 * 
 * @see <a href="http://docs.cor-lab.de/rsb-manual/trunk/html/specification.html#tcp-socket-based-transport">RSB Specification for Socket Transport</a>
 * 
 * @author swrede
 *
 */
public class BusConnection {
	
	private static Logger log = Logger.getLogger(BusConnection.class.getName());	

	protected static final int HANDSHAKE        = 0x00000000;
	
	Socket socket;
	ReadableByteChannel reader;
	WritableByteChannel writer;	
	InetAddress address;
	int port;
	boolean isServer = false;
	
	public BusConnection(InetAddress addr, int port, boolean isServer) {
		address = addr;
		this.port = port;
		this.isServer = isServer;
	}
	
	public BusConnection(InetAddress addr, int port) {
		address = addr;
		this.port = port;
	}

	public void activate() throws IOException, RSBException {
		if (isServer) {
			// TODO instantiate server socket
			throw new RuntimeException("Not implemented yet!");
		} else {
			// instantiate Socket object
			socket = new Socket(address,port);
			log.info("Client Socket established at local port: " + socket.getLocalPort());

			// get i/o streams 
			reader = Channels.newChannel(socket.getInputStream());
			writer = Channels.newChannel(socket.getOutputStream());
		}
		// do RSB handshake
		connect();
		
	}		

	/**
	 * Safely close I/O streams and sockets.
	 *
	 * @throws RSBException 
	 */	
	public void deactivate() {
			try {	
				if (writer!=null && writer.isOpen()) writer.close();
				if (reader!=null && reader.isOpen()) reader.close();				
				if (socket!=null && socket.isConnected()) socket.close();
			} catch (IOException e) {
				log.warning("Exception during deactivation of BusConnection: " + e.getMessage());
			}
	}
	
	/**
	 * Perform simple handshake as specified in RSB socket protocol.
	 *
	 * @throws RSBException 
	 */
	protected void connect() throws RSBException {
		ByteBuffer buf_handshake = ByteBuffer.allocateDirect(4);
		buf_handshake.asIntBuffer().put(HANDSHAKE);
					
		System.out.print("Request:" + buf_handshake.getInt());
		try {
			writer.write(buf_handshake);
			// read
			ByteBuffer bb = ByteBuffer.allocateDirect(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);	
			System.out.println("Bytes read: " + reader.read(bb));
			// check if reply = 0x00000000;
			bb.rewind();
			if (HANDSHAKE==bb.getInt()) {
				log.info("RSB Handshake successfull!");
			} else {
				throw new RSBException("RSB Handshake failed in SocketTransport at " + address.toString() + ":" + port);
			}
		} catch (IOException e) {
			throw new RSBException(e);
		} // write(HANDSHAKE);
	}	
	
	/**
	 * Extract length of next notification blob.
	 * 
	 * @return Number of bytes
	 * @throws IOException
	 */
	protected int readLength() throws IOException {
		ByteBuffer bb = ByteBuffer.allocateDirect(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println("Bytes read: " + reader.read(bb));
		bb.rewind();
		return bb.getInt();
	}	

	/**
	 * Read a single Socket transport packet (length + notification)
	 * and decode it into an RSB notification object.
	 * 
	 * @return Notification object
	 * @throws IOException
	 */	
	protected Notification readNotification() throws IOException {
		// get size
		int length = readLength();
		byte[] buf = new byte[length];
		ByteBuffer buf_notification = ByteBuffer.wrap(buf);
		// Why does the following not work?!?
		// ByteBuffer buf_notification = ByteBuffer.allocateDirect(length);
		buf_notification.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println("Bytes to be read: " + buf_notification.remaining());
		Notification n = null;
		try {
			System.out.println("Bytes read: " + reader.read(buf_notification));
			//saveRawNotification(buf_notification);
			buf_notification.rewind();
			n = Notification.parseFrom(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}	
	
	
}
