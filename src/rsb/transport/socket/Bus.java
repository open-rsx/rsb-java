package rsb.transport.socket;

import rsb.RSBException;
import rsb.Activatable;
import rsb.protocol.NotificationType.Notification;

/**
 * Instances of this class provide access to a socket-based bus. It is
 * transparent for clients (connectors) of this class whether it is accessed by
 * running the bus server or by connecting to the bus server as a client.
 *
 * @author jwienke
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Bus extends Activatable {

    /**
     * Interfaces for observers of {@link Bus} instances that want to
     * asynchronously receive incoming notifications.
     *
     * @author jwienke
     */
    interface NotificationReceiver {

        /**
         * Callback method with the received notification.
         *
         * @param notification
         *            the new notification
         */
        void handle(Notification notification);

    }

    /**
     * Returns the current socket configuration of the bus.
     *
     * @return socket options used
     */
    SocketOptions getSocketOptions();

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
    void handleOutgoing(Notification notification) throws RSBException;

    /**
     * Registers a local observer for notifications.
     *
     * @param receiver
     *            the receiver to register
     */
    void addNotificationReceiver(NotificationReceiver receiver);

}
