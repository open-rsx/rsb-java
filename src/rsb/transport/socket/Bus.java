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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;
import rsb.util.Properties;

/**
 * Instances of this class provide access to a socket-based bus.
 * It is transparent for clients (connectors) of this class whether
 * is accessed by running the bus server or by connecting to the bus
 * server as a client.
 *
 * This class offers methods for sending and receiving events to this
 * bus as well as registration of internal Connectors (inward) and
 * Connections (outward) which allow to send event notifications to
 * external participants.
 *
 * @author swrede
 *
 */
public abstract class Bus {

	private final static Logger log = Logger.getLogger(Bus.class.getName());
	private final static rsb.util.Properties prop = Properties.getInstance();
	InetAddress address;
	int port;

	ConcurrentLinkedQueue<BusConnection> connections = new ConcurrentLinkedQueue<BusConnection>();

	public void handleIncoming() {
		// TODO handle incoming notifications and dispatch these to connectors
	}

	/**
	 * Distribute event notification to connected participants.
	 */
	public void handleOutgoing(Notification notification) {
		// TODO check if Bus needs to be locked
		// 1. Broadcast notification to connections
		for (BusConnection con : connections) {
			// TODO add exception handling
			con.sendNotification(notification);
		}
		// TODO 2. Broadcast notification to internal connectors
	}

	public void addConnection(BusConnection con) {
		connections.add(con);
	}

	public void removeConnection(BusConnection con) {
		if (connections.remove(con)==false) {
			log.warning("Couldn't remove BusConnection " + con + " from connection queue.");
		}
	}

// TODO implement InPushConnector support for internal notification

	public static Bus createBusClient() throws IOException, RSBException {
		InetAddress addr = InetAddress.getByName(prop.getProperty("transport.socket.host"));
		BusClient client = new BusClient(addr, prop.getPropertyAsInt("transport.socket.port"));
		client.activate();
		return client;
	}

	public static Bus createBusServer() throws IOException {
		InetAddress addr = InetAddress.getByName(prop.getProperty("transport.socket.host"));
		BusServer server = new BusServer(addr, prop.getPropertyAsInt("transport.socket.port"));
		server.activate();
		return server;
	}
	
	
}
