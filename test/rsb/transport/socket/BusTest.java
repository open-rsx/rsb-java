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
		Bus bus = new BusClient(InetAddress.getLocalHost(), 55555);
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
		Bus bus = new BusClient(InetAddress.getLocalHost(), 55555);
		assertTrue(bus.connections.isEmpty());
		BusConnection con = new BusConnection(InetAddress.getLocalHost(), 55555);
		bus.addConnection(con);
		assertTrue(bus.connections.size()==1);
		bus.removeConnection(con);
		assertTrue(bus.connections.isEmpty());
	}

}
