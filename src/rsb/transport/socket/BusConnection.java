package rsb.transport.socket;

import java.io.IOException;

import rsb.RSBException;
import rsb.RSBObject;
import rsb.protocol.NotificationType.Notification;

/**
 * Interface for connections to the socket based transport.
 *
 * @author jwienke
 */
public interface BusConnection extends RSBObject {

    /**
     * Sends a notification over the connection.
     *
     * @param notification
     *            the notification to send. Must be complete so that it can be
     *            serialized.
     * @throws IOException
     *             error sending the notification
     */
    void sendNotification(Notification notification) throws IOException;

    /**
     * Reads a notification from the connection. Blocks if necessary.
     *
     * @return the read notification
     * @throws IOException
     *             communication error
     */
    Notification readNotification() throws IOException;

    /**
     * Performs the handshake step of the protocol. Must be called after
     * {@link #activate()}.
     *
     * @throws RSBException
     *             error during handshake
     */
    void handshake() throws RSBException;

    /**
     * Returns the current configuration of the connection.
     *
     * @return configuration of the connection
     */
    SocketOptions getOptions();

}
