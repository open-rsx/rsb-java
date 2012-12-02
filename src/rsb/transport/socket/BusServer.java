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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import rsb.RSBException;

/**
 *  Instances of this class provide access to a socket-based bus for 
 *  remote bus clients. Remote clients connect to a server socket in 
 *  order to send and receive events through the resulting socket connection 
 *  (maintained in BusConnection objects). 
 *  
 *  Local clients (connectors) use the usual Bus interface to receive
 *  events published by remote clients and submit events which will be 
 *  distributed to remote clients by the BusServer through the list of 
 *  active BusConnection instances.
 * 
 *  @author swrede
 *
 */
public class BusServer extends Bus implements Runnable {

	private static final Logger log = Logger.getLogger(BusServer.class.getName());
	private ServerSocket serverSocket;
	protected ExecutorService pool;	
	private boolean isShutdown = false;

	public BusServer(InetAddress host, int port) {
		this.address = host;
		this.port = port;
	}
	
	public void activate() throws IOException {
	    pool = Executors.newCachedThreadPool();
		serverSocket = new ServerSocket(port);	
	}
	
	public void deactivate() {
		this.isShutdown = true;
		log.info("BusServer terminating");
		pool.shutdown();  
		try {
			// wait for termination of active workers
			pool.awaitTermination(4L, TimeUnit.SECONDS);
			// exit run loop by closing the socket
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (IOException e) {
			// ignore
		} catch (InterruptedException ei) {
			// ignore
		}
	}	
	
	@Override
	public void run() {
			while ( true && !isShutdown ) {		
				Socket socket = null;
				
				// socket handling
				try {
					// accept socket
					log.info("waiting for new client connection");
					socket = serverSocket.accept();  
				} catch (IOException ex) {
					log.info("BusServer interrupted on socket.accept!");
					if (!isShutdown) deactivate();
				}
				
				// setup new RSB BusConnection for the client
				if (socket!=null && !isShutdown) {
					// start BusConnection worker to serve this client
					BusConnection worker = new BusConnection(socket,true);
					try {
						worker.activate();
						worker.handshake();
						// add BusConnection instance to list of active connections
						this.addConnection(worker);
						// worker fully constructed, schedule for execution
						pool.execute(worker);
					} catch (IOException e) {
						// should not happen
						e.printStackTrace();
					} catch (RSBException e) {
						// should not happen
						e.printStackTrace();
					}
				}

			}
	}
}
