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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

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
 * @author jwienke
 */
public class BusServer extends BusBase {

    private static final Logger LOG = Logger.getLogger(BusServer.class
            .getName());
    private ServerSocket serverSocket;
    private AcceptorThread acceptor;

    /**
     * A thread that listens on a {@link ServerSocket} and accepts connection
     * requests by instantiating new {@link BusServerConnection} handling each
     * new connection request.
     *
     * @author jwienke
     */
    private class AcceptorThread extends Thread {

        private volatile boolean shutdown = false;

        /**
         * Indicates that a termination should be performed. There is no
         * guarantee when this will happen after a call to this method.
         */
        public void startShutdown() {
            this.shutdown = true;
        }

        @Override
        @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
        public void run() {

            while (!this.shutdown) {

                // socket handling
                try {
                    // accept socket
                    LOG.info("Waiting for new client connection");
                    @SuppressWarnings("resource")
                    final Socket socket = BusServer.this.serverSocket.accept();
                    LOG.log(Level.FINE, "Accepted a new client socket: {0}",
                            socket);

                    final BusServerConnection connection =
                            new BusServerConnection(socket, getSocketOptions()
                                    .isTcpNoDelay());
                    // we need to lock this whole procedure to ensure that no
                    // new messages are sent to unfinished new connection or
                    // missed in the dispatching logic for clients of the server
                    synchronized (BusServer.this) {
                        // add BusConnection instance to list of active
                        // connections. This must be done BEFORE performing the
                        // handshake. Otherwise, a BusClient.activate call might
                        // return before this connection is really installed in
                        // our own dispatching logic.
                        // This logic leads to the situation that inactive
                        // connections are in the connection list and might be
                        // called to dispatch incoming notifications. This case
                        // needs to be handled explicitly.
                        final ReceiveThread thread = addConnection(connection);
                        // now we are safe to perform the handshake as
                        // everything else is set up
                        connection.activate();
                        // We need to wait with starting the receive thread
                        // until the connection is fully functional
                        thread.start();
                    }
                    LOG.log(Level.FINER,
                            "Activated a new client connection {0} "
                                    + "for socket {1}", new Object[] {
                                    connection, socket });

                } catch (final IOException e) {
                    if (this.shutdown) {
                        LOG.log(Level.INFO,
                                "Shutting down due to request and closed socket.");
                    } else {
                        LOG.log(Level.WARNING,
                                "Unexpected exception while accepting "
                                        + "new client. Shutting down.", e);
                    }
                    return;
                } catch (final RSBException e) {
                    LOG.log(Level.WARNING,
                            "Unexpected exception while accepting new client.",
                            e);
                    // TODO what to do here?
                }

            }

        }

    }

    /**
     * Constructor.
     *
     * @param options
     *            socket options to use for the communication
     */
    public BusServer(final SocketOptions options) {
        super(options);
    }

    @Override
    public void activate() throws RSBException {

        LOG.fine("Trying to activate BusServer.");

        synchronized (this) {

            if (this.serverSocket != null) {
                throw new IllegalStateException("BusServer is already active.");
            }

            try {
                this.serverSocket =
                        new ServerSocket(this.getSocketOptions().getPort());
                this.acceptor = new AcceptorThread();
                this.acceptor.start();
            } catch (final IOException e) {
                throw new RSBException(e);
            }

        }

    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        LOG.info("Trying to deactivate BusServer.");

        synchronized (this) {

            if (this.serverSocket == null) {
                throw new IllegalStateException("BusServer is not active.");
            }

            try {

                this.acceptor.startShutdown();
                this.serverSocket.close();
                this.acceptor.join();

            } catch (final IOException e) {
                LOG.log(Level.WARNING, "Exception closing server socket.", e);
            } catch (final InterruptedException e) {
                LOG.log(Level.WARNING,
                        "Interrupted while waiting for acceptor thread to terminate.",
                        e);
            }

            super.deactivate();

            this.serverSocket = null;
            this.acceptor = null;

        }

    }

    @Override
    public boolean isActive() {
        return this.serverSocket != null;
    }

    @Override
    public void handleIncoming(final Notification notification,
            final BusConnection sourceConnection) throws RSBException {
        handleLocally(notification);
        handleGlobally(notification, sourceConnection);
    }

}
