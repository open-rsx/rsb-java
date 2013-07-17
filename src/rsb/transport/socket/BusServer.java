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
 * Instances of this class provide access to a socket-based bus for remote bus
 * clients. Remote clients connect to a server socket in order to send and
 * receive events through the resulting socket connection (maintained in
 * BusConnection objects).
 *
 * Local clients (connectors) use the usual Bus interface to receive events
 * published by remote clients and submit events which will be distributed to
 * remote clients by the BusServer through the list of active BusConnection
 * instances.
 *
 * @author swrede
 */
public class BusServer extends Bus implements Runnable {

    private static final Logger LOG = Logger.getLogger(BusServer.class
            .getName());
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private boolean isShutdown = false;

    public BusServer(final InetAddress host, final int port) {
        this.setAddress(host);
        this.setPort(port);
    }

    public void activate() throws IOException {
        this.pool = Executors.newCachedThreadPool();
        this.serverSocket = new ServerSocket(this.getPort());
    }

    public void deactivate() {
        this.isShutdown = true;
        LOG.info("BusServer terminating");
        this.pool.shutdown();
        try {
            // wait for termination of active workers
            this.pool.awaitTermination(4L, TimeUnit.SECONDS);
            // exit run loop by closing the socket
            if (!this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
        } catch (final IOException e) {
            // ignore
        } catch (final InterruptedException ei) {
            // ignore
        }
    }

    @Override
    public void run() {
        while (true && !this.isShutdown) {
            Socket socket = null;

            // socket handling
            try {
                // accept socket
                LOG.info("waiting for new client connection");
                socket = this.serverSocket.accept();
            } catch (final IOException ex) {
                LOG.info("BusServer interrupted on socket.accept!");
                if (!this.isShutdown) {
                    this.deactivate();
                }
            }

            // setup new RSB BusConnection for the client
            if (socket != null && !this.isShutdown) {
                // start BusConnection worker to serve this client
                final BusServerConnection worker = new BusServerConnection(
                        socket);
                try {
                    worker.activate();
                    worker.handshake();
                    // add BusConnection instance to list of active connections
                    this.addConnection(worker);
                    // worker fully constructed, schedule for execution
                    // TODO reenable this with an external thread
                    // this.pool.execute(worker);
                } catch (final RSBException e) {
                    // should not happen
                    e.printStackTrace();
                }
            }

        }
    }
}
