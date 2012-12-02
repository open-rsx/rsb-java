/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
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

import rsb.Event;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;

/**
 * Instances of this class implement connections to a socket-based
 * bus.
 *
 * The basic operations provided by this class are receiving an event
 * notifications by calling receiveNotification and submitting an
 * event to the bus by calling sendNotification. This class implements
 * the fundamental RSB protocol for socket connections, e.g., the
 * basic handshaking and the encoding/decoding of data packages for
 * event notifications.
 *
 * @see <a href="http://docs.cor-lab.de/rsb-manual/trunk/html/specification-socket.html">RSB Specification for Socket Transport</a>
 *
 * @author swrede
 *
 */
public class BusConnection implements Runnable {

	private static Logger log = Logger.getLogger(BusConnection.class.getName());

	protected static final int HANDSHAKE        = 0x00000000;

	Socket socket;
	ReadableByteChannel reader;
	WritableByteChannel writer;
	InetAddress address;
	int port;
	boolean isServer = false;
	boolean isShutdown = false;

	protected BusConnection(InetAddress addr, int port, boolean isServer) {
		address = addr;
		this.port = port;
		this.isServer = isServer;
	}

	protected BusConnection(InetAddress addr, int port) {
		address = addr;
		this.port = port;
	}

	public BusConnection(Socket socket, boolean isServer) {
		this.socket = socket;
		this.port = socket.getLocalPort();
		this.address = socket.getInetAddress();
		this.isServer = isServer;
	}

	public void activate() throws IOException, RSBException {
		if (socket == null && !isServer) {
			socket = new Socket(address,port);
		} else {
			throw new RSBException("Invalid call to activate. Socket equals null and server mode requested.");
		}
		reader = Channels.newChannel(socket.getInputStream());
		writer = Channels.newChannel(socket.getOutputStream());
	}

	/**
	 * Perform simple handshake as specified in RSB socket protocol.
	 *
	 * @throws RSBException
	 */
	public void handshake() throws RSBException {
		if (isServer) {
			// TODO implement server handshake protocol
		} else {
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
	}	
	
	/**
	 * Safely close I/O streams and sockets.
	 *
	 * @throws RSBException
	 */
	public void deactivate() {
		this.isShutdown = true;
		try {
			if (writer!=null && writer.isOpen()) writer.close();
			if (reader!=null && reader.isOpen()) reader.close();
			if (socket!=null && socket.isConnected()) socket.close();
		} catch (IOException e) {
			log.warning("Exception during deactivation of BusConnection: " + e.getMessage());
		}
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
	public Notification readNotification() throws IOException {
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

	public void run() {
		// TODO implement reading from Socket
		// process packet
		int i = 0;
		while (!isShutdown) {
			i++;
			System.out.println("Waiting for Notification #" + i);
			Notification n;
			try {
				n = this.readNotification();
				// convert to Event
				Event e = EventBuilder.fromNotification(n);
	
				System.out.println("Scope: " + e.getScope());
				System.out.println("Id: " + e.getId());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}		
	}	
	
	public void sendNotification(Notification notification) {
		// TODO implement sending of notification to socket
	}


}
