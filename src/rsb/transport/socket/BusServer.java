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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author swrede
 *
 */
public class BusServer extends Bus implements Runnable {

	Logger log = Logger.getLogger(BusServer.class.getName());
	private ServerSocket server;

	public BusServer(InetAddress host, int port) throws IOException {
		server = new ServerSocket(55555);
	}

	public void acceptClient() {
		// super.addConnection(newSocket);
	}

	@Override
	public void run() {
		try {

			System.out.println("Redirecting connections on port " + server.getLocalPort());// + " to " + newSite);

			while (true) {

				try {
					Socket s = server.accept();
					Thread t = new RedirectThread(s);
					t.start();
				} // end try
				catch (IOException e) {
				}

			} // end while

		} // end try
		catch (Exception e) {
			// TODO handle specific execeptions
		}
//		catch (BindException e) {
//			System.err.println("Could not start server. Port Occupied");
//		} catch (IOException e) {
//			System.err.println(e);
//		}

	}

}
