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
import java.nio.channels.AsynchronousCloseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.RSBObject;
import rsb.protocol.NotificationType.Notification;

/**
 * Instances of this class provide access to a socket-based bus. It is
 * transparent for clients (connectors) of this class whether it is accessed by
 * running the bus server or by connecting to the bus server as a client.
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
@SuppressWarnings("PMD.ShortClassName")
public abstract class Bus implements RSBObject {

    private final static Logger LOG = Logger.getLogger(Bus.class.getName());
    private final InetAddress address;
    private final int port;
    private final Map<BusConnection, ReceiveThread> connections = Collections
            .synchronizedMap(new HashMap<BusConnection, ReceiveThread>());
    private final Set<NotificationReceiver> receivers = Collections
            .synchronizedSet(new HashSet<NotificationReceiver>());

    /**
     * A thread that continuously reads from a {@link BusConnection} and passes
     * the received {@link Notification}s to
     * {@link Bus#handleIncoming(Notification)}.
     *
     * @author jwienke
     */
    private class ReceiveThread extends Thread {

        // no chance to make this logger final in a not final member class
        @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
        private final Logger logger = Logger.getLogger(ReceiveThread.class
                .getName());

        private final BusConnection connection;

        /**
         * Constructs a new instance operating on the specified connection
         * instance.
         *
         * @param connection
         *            the connection to read from
         */
        public ReceiveThread(final BusConnection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {

            while (true) {

                try {
                    this.logger.finer("Waiting for a new notification.");
                    final Notification notification = this.connection
                            .readNotification();
                    handleIncoming(notification);
                } catch (final AsynchronousCloseException e) {
                    this.logger.log(Level.FINE,
                            "Connection has been closed. Terminating.", e);
                    return;
                } catch (final IOException e) {
                    this.logger
                            .log(Level.WARNING,
                                    "Error while reading a new notification from the bus connection. Shutting down. Most likely this is actually desired.",
                                    e);
                    // TODO how to differentiate between real errors and a
                    // socket close in a better way?
                    return;
                } catch (final RSBException e) {
                    this.logger
                            .log(Level.WARNING,
                                    "Unable to correctly handle a notiication. "
                                            + "Continuiung with the next one and ignoring this error.",
                                    e);
                }

            }

        }
    }

    /**
     * Interfaces for observers of {@link Bus} instances that want to
     * asynchronously receive incoming notifications.
     *
     * @author jwienke
     */
    public interface NotificationReceiver {

        /**
         * Callback method with the received notification.
         *
         * @param notification
         *            the new notification
         */
        void handle(Notification notification);

    }

    protected Bus(final InetAddress address, final int port) {
        assert address != null;
        assert port > 0;
        this.address = address;
        this.port = port;
    }

    /**
     * Returns the IP address this Bus operates on.
     *
     * @return IP address
     */
    public InetAddress getAddress() {
        return this.address;
    }

    /**
     * Returns the port this bus operations on.
     *
     * @return bus port
     */
    public int getPort() {
        return this.port;
    }

    @Override
    public void deactivate() throws RSBException {

        try {

            synchronized (this.connections) {
                // terminate available connections and associated receiver
                // threads
                for (final Entry<BusConnection, ReceiveThread> threadByConn : this.connections
                        .entrySet()) {
                    threadByConn.getKey().deactivate();
                    threadByConn.getValue().join();
                }
                this.connections.clear();
            }

        } catch (final InterruptedException e) {
            throw new RSBException(
                    "Interrupted while waiting for receiver threads to finish.",
                    e);
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
     * @throws RSBException
     *             processing error
     */
    public abstract void handleIncoming(final Notification notification)
            throws RSBException;

    /**
     * Dispatches the specified notifications to all registered
     * {@link NotificationReceiver}s.
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
     * @throws RSBException
     *             error during dispatching
     */
    protected void handleGlobally(final Notification notification)
            throws RSBException {
        LOG.fine("Dispatching notification to bus connections");

        synchronized (this.connections) {
            for (final BusConnection con : this.connections.keySet()) {
                try {
                    con.sendNotification(notification);
                } catch (final IOException e) {
                    throw new RSBException(
                            "Unable to send notification on connection " + con,
                            e);
                }
            }
        }

    }

    /**
     * Handles a notification to be sent over the bus.
     *
     * The default implementation dispatches the notification to all local
     * {@link NotificationReceiver} and to all registered {@link BusConnection}
     * s.
     *
     * @param notification
     *            the notification to distribute
     * @throws RSBException
     *             error during dispatching
     */
    public void handleOutgoing(final Notification notification)
            throws RSBException {
        handleLocally(notification);
        handleGlobally(notification);
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
        synchronized (this.connections) {
            if (this.connections.containsKey(con)) {
                throw new IllegalArgumentException("Connection " + con
                        + " is already registered.");
            }
            final ReceiveThread receiveThread = new ReceiveThread(con);
            receiveThread.start();
            LOG.log(Level.FINER,
                    "Started receiver thread {0} for this connection {1}.",
                    new Object[] { receiveThread, con });
            this.connections.put(con, receiveThread);
        }
    }

    /**
     * Removes a connection from the dispatching logic.
     *
     * @param con
     *            the connection to remove
     */
    protected void removeConnection(final BusConnection con) {
        if (this.connections.remove(con) == null) {
            LOG.warning("Couldn't remove BusConnection " + con
                    + " from connection queue.");
        }
    }

    /**
     * Indicates how many connections are currently registered.
     *
     * @return number of connections
     */
    public int numberOfConnections() {
        return this.connections.size();
    }

    /**
     * Registers a local observer for notifications.
     *
     * @param receiver
     *            the receiver to register
     */
    public void addNotificationReceiver(final NotificationReceiver receiver) {
        this.receivers.add(receiver);
    }

}
