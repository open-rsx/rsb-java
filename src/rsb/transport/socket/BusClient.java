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

import rsb.RSBException;

/**
 * @author swrede
 *
 */
public class BusClient extends Bus {
	
	BusConnection connection;
	Thread worker;

	public BusClient(InetAddress host, int port) {
		this.address = host;
		this.port = port;
	}

	public void activate() throws IOException, RSBException {
		connection = new BusConnection(address, port);
		connection.activate();
		connection.handshake();
		worker = new Thread(connection);
		worker.start();
		super.addConnection(connection);
	}	
	
	public void deactivate() {
		if (connection!=null) {
			connection.deactivate();
		}
	}
}
