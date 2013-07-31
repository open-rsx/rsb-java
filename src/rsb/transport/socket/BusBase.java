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

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * A utility base class for the implementation of the {@link Bus} interface.
 *
 * This class offers methods for sending and receiving events to this bus as
 * well as registration of internal Connectors (inward) and Connections
 * (outward) which allow to send event notifications to external participants.
 *
 * Subclasses of this class should use this instance to synchronize on.
 *
 * Subclasses should call {@link #deactivate()} when overriding this method to
 * ensure that receiving threads are terminated properly.
 *
 * @author swrede
 * @author jwienke
 */
public abstract class BusBase implements Bus {

    private final static Logger LOG = Logger.getLogger(Bus.class.getName());
    private final SocketOptions options;
    private final Map<BusConnection, ReceiveThread> connections = Collections
            .synchronizedMap(new HashMap<BusConnection, ReceiveThread>());
    private final Set<NotificationReceiver> receivers = Collections
            .synchronizedSet(new HashSet<NotificationReceiver>());

    /**
     * A thread that continuously reads from a {@link BusConnection} and passes
     * the received {@link Notification}s to
     * {@link BusBase#handleIncoming(Notification)}.
     *
     * @author jwienke
     */
    private class ReceiveThread extends Thread {

        // no chance to make this logger final in a not final member class
        @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
        private final Logger logger = Logger.getLogger(ReceiveThread.class
                .getName());

        private final BusConnection connection;
        private final Runnable eofHandler;

        /**
         * Constructs a new instance operating on the specified connection
         * instance.
         *
         * @param connection
         *            the connection to read from
         * @param eofHandler
         *            code to execute in case this receiving thread receives an
         *            EOF, which indicates that the other endpoint of the
         *            connection being received on terminates
         */
        public ReceiveThread(final BusConnection connection,
                final Runnable eofHandler) {
            this.connection = connection;
            this.eofHandler = eofHandler;
        }

        @Override
        public void run() {

            while (true) {

                try {
                    this.logger.finer("Waiting for a new notification.");
                    final Notification notification = this.connection
                            .readNotification();
                    handleIncoming(notification, this.connection);
                } catch (final EOFException e) {
                    this.logger.log(Level.FINE,
                                    "End of stream from remote peer on connection "
                                            + this.connection
                                            + ". Calling handler.",
                            e);
                    this.eofHandler.run();
                    return;
                } catch (final AsynchronousCloseException e) {
                    this.logger.log(Level.INFO,
                            "Connection has been closed. Terminating.", e);
                    final ReceiveThread thread = removeConnection(this.connection);
                    assert thread == this;
                    return;
                } catch (final IOException e) {
                    this.logger
                            .log(Level.WARNING,
                                    "Error while reading a new notification from the bus connection. Shutting down. Most likely this is actually desired.",
                                    e);
                    final ReceiveThread thread = removeConnection(this.connection);
                    assert thread == this;
                    return;
                } catch (final RSBException e) {
                    this.logger
                            .log(Level.WARNING,
                                    "Unable to correctly handle a notification. "
                                            + "Continuing with the next one and ignoring this error.",
                                    e);
                }

            }

        }
    }

    protected BusBase(final SocketOptions options) {
        assert options != null;
        this.options = options;
    }

    @Override
    public SocketOptions getSocketOptions() {
        return this.options;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void deactivate() throws RSBException, InterruptedException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Bus is not active.");
            }

            try {

                // terminate available connections and associated receiver
                // threads
                for (final BusConnection connection : new HashSet<BusConnection>(
                        this.connections.keySet())) {
                    final ReceiveThread thread = removeConnection(connection);
                    thread.interrupt();
                    thread.join();
                    synchronized (connection) {
                        if (connection.isActive()) {
                            connection.deactivate();
                        }
                    }
                }
                this.connections.clear();

            } catch (final InterruptedException e) {
                throw new RSBException(
                        "Interrupted while waiting for receiver threads to finish.",
                        e);
            }

        }

    }

    /**
     * Implement this method to specify the behavior in case of an incoming
     * notification received from a connection.
     *
     * Implementations can use the utility methods
     * {@link #handleLocally(Notification)} and
     * {@link #handleGlobally(Notification)} to implement their processing
     * logic.
     *
     * @param notification
     *            the received connection
     * @param sourceConnection
     *            the source {@link BusConnection} providing this Notification
     * @throws RSBException
     *             processing error
     */
    public abstract void handleIncoming(final Notification notification,
            BusConnection sourceConnection) throws RSBException;

    /**
     * Implement this method to specify the behavior in case of an incoming
     * notification received from a connection.
     *
     * Implementations can use the utility methods
     * {@link #handleLocally(Notification)} and
     * {@link #handleGlobally(Notification)} or
     * {@link #handleGlobally(Notification, BusConnection)} to implement their
     * processing logic.
     *
     * This method assumes that the notification was not received from a
     * {@link BusConnection} and passes <code>null</code> to
     * {@link #handleIncoming(Notification, BusConnection)}.
     *
     * @param notification
     *            the received connection
     * @throws RSBException
     *             processing error
     */
    public void handleIncoming(final Notification notification)
            throws RSBException {
        handleIncoming(notification, null);
    }

    /**
     * Dispatches the specified notifications to all registered
     * {@link rsb.transport.socket.Bus.NotificationReceiver}s.
     *
     * @param notification
     *            the notification to dispatch
     * @throws RSBException
     *             exception during dispatching
     */
    protected void handleLocally(final Notification notification)
            throws RSBException {
        LOG.fine("Dispatching notification to local NotificationReceivers.");

        // TODO terrible synchronization. Blocks too long
        synchronized (this.receivers) {
            for (final NotificationReceiver receiver : this.receivers) {
                receiver.handle(notification);
            }
        }

    }

    /**
     * Dispatches the notification to registered connections.
     *
     * @param notification
     *            notification to dispatch
     * @param ignoreConnection
     *            for dispatching, ignore this connection and do not pass the
     *            notification to this instance. Might be <code>null</code> if
     *            this filtering is not required.
     * @throws RSBException
     *             error during dispatching
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    protected void handleGlobally(final Notification notification,
            final BusConnection ignoreConnection) throws RSBException {
        LOG.fine("Dispatching notification to bus connections");

        // makes an atomic copy of the available connections to prevent blocking
        for (final BusConnection con : new HashSet<BusConnection>(
                this.connections.keySet())) {
            if (con.equals(ignoreConnection)) {
                continue;
            }
            try {
                con.sendNotification(notification);
            } catch (final IOException e) {
                LOG.log(Level.WARNING,
                        "Unable to send notification on connection " + con
                                + ". Removing this connection.", e);
                // as the connection is obviously broken now, we assume that the
                // receiving thread will automatically terminate
                removeConnection(con);
            }
        }

    }

    /**
     * Dispatches the notification to registered connections.
     *
     * @param notification
     *            notification to dispatch
     * @throws RSBException
     *             error during dispatching
     */
    protected void handleGlobally(final Notification notification)
            throws RSBException {
        handleGlobally(notification, null);
    }

    @Override
    public void handleOutgoing(final Notification notification)
            throws RSBException {
        handleLocally(notification);
        handleGlobally(notification);
    }

    /**
     *
     * @author jwienke
     */
    private class CloseConnection implements Runnable {

        // no chance to make this logger final in a not final member class
        @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
        private final Logger logger = Logger.getLogger(CloseConnection.class
                .getName());

        private final BusConnection connection;

        public CloseConnection(final BusConnection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                this.logger.log(Level.FINE,
                        "If still active, trying to deactivate connection {0}",
                        this.connection);
                synchronized (this.connection) {
                    if (this.connection.isActive()) {
                        this.connection.deactivate();
                    }
                    // in this case we know that the receiving thread will
                    // terminate immediately, so we do not have to take care of
                    // this.
                    removeConnection(this.connection);
                }
            } catch (final Exception ex) {
                this.logger.log(Level.WARNING,
                        "Error during deactivation of connection "
                                + this.connection, ex);
            }
        }

    }

    /**
     * Registers a connection for the dispatching logic in
     * {@link #handleGlobally(Notification)}.
     *
     * @param con
     *            the connection to register
     */
    protected void addConnection(final BusConnection con) {
        LOG.log(Level.FINE, "Adding a new BusConnection: {0}", con);
        synchronized (this) {
            if (this.connections.containsKey(con)) {
                throw new IllegalArgumentException("Connection " + con
                        + " is already registered.");
            }
            final ReceiveThread receiveThread = new ReceiveThread(con,
                    new CloseConnection(con));
            receiveThread.start();
            LOG.log(Level.FINER,
                    "Started receiver thread {0} for this connection {1}.",
                    new Object[] { receiveThread, con });
            this.connections.put(con, receiveThread);
        }
    }

    /**
     * Removes a connection from the dispatching logic. The connection is
     * neither closed automatically nor is the receiving thread terminated.
     *
     * @param con
     *            the connection to remove
     * @return the {@link ReceiveThread} responsible for this connection or
     *         <code>null</code> if this connection was not part of the
     *         dispatching logic
     */
    protected ReceiveThread removeConnection(final BusConnection con) {

        synchronized (this.connections) {

            if (!this.connections.containsKey(con)) {
                return null;
            }

            final ReceiveThread receiver = this.connections.get(con);

            if (this.connections.remove(con) == null) {
                LOG.warning("Couldn't remove BusConnection " + con
                        + " from connection list.");
            }

            return receiver;

        }

    }

    /**
     * Indicates how many connections are currently registered.
     *
     * @return number of connections
     */
    public int numberOfConnections() {
        synchronized (this) {
            return this.connections.size();
        }
    }

    @Override
    public void addNotificationReceiver(final NotificationReceiver receiver) {
        this.receivers.add(receiver);
    }

}
