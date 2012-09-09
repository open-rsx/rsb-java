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
