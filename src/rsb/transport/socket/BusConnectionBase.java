package rsb.transport.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * Utility base class for the implementation of the {@link BusConnection}
 * interface.
 *
 * Subclasses need to use this object for synchronization.
 *
 * Subclasses need to call {@link #setSocket(Socket)} with a valid socket before
 * {@link #activate()} will be called.
 *
 * @author jwienke
 */
public abstract class BusConnectionBase implements BusConnection {

    private static final Logger LOG = Logger.getLogger(BusConnectionBase.class
            .getName());

    private Socket socket;
    private ReadableByteChannel reader;
    private WritableByteChannel writer;
    private InetAddress address;
    private int port;

    public Socket getSocket() {
        return this.socket;
    }

    protected void setSocket(final Socket socket) {
        synchronized (this) {
            this.socket = socket;
        }
    }

    public InetAddress getAddress() {
        return this.address;
    }

    protected void setAddress(final InetAddress address) {
        synchronized (this) {
            this.address = address;
        }
    }

    public int getPort() {
        return this.port;
    }

    protected void setPort(final int port) {
        synchronized (this) {
            this.port = port;
        }
    }

    protected ReadableByteChannel getReader() {
        return this.reader;
    }

    protected WritableByteChannel getWriter() {
        return this.writer;
    }

    @Override
    public void activate() throws RSBException {

        synchronized (this) {
            assert this.socket != null;
            try {
                this.reader = Channels.newChannel(getSocket().getInputStream());
                this.writer = Channels
                        .newChannel(getSocket().getOutputStream());
            } catch (final IOException e) {
                this.reader = null;
                this.writer = null;
                throw new RSBException(e);
            }
        }

    }

    /**
     * Safely close I/O streams and sockets.
     */
    @Override
    public void deactivate() throws RSBException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Connection is not active.");
            }

            try {
                this.writer.close();
                this.reader.close();
                this.socket.close();
            } catch (final IOException e) {
                LOG.log(Level.WARNING,
                        "Exception during deactivation of BusConnectionBase. "
                                + "Ignoring this exception and doing so as if nothing happened.",
                        e);
            }

            this.reader = null;
            this.writer = null;

        }

    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.writer != null;
        }
    }

    /**
     * Extract length of next notification blob.
     *
     * @return Number of bytes
     * @throws IOException
     */
    protected int readLength() throws IOException {
        assert isActive();
        final ByteBuffer lengthBytes = ByteBuffer
                .allocateDirect(Protocol.DATA_SIZE_BYTES);
        lengthBytes.order(ByteOrder.LITTLE_ENDIAN);
        final int bytesRead = this.reader.read(lengthBytes);
        if (bytesRead != Protocol.DATA_SIZE_BYTES) {
            throw new IOException(
                    "Unexpected number of bytes received for the specification "
                            + "of the next notification blob to receive.");
        }
        lengthBytes.rewind();
        return lengthBytes.getInt();
    }

    @Override
    public Notification readNotification() throws IOException {

        if (!isActive()) {
            throw new IllegalStateException(
                    "Cannot read. Connection is not active.");
        }

        LOG.fine("Starting to read a notification from the wire.");

        // read expected size
        final int length = this.readLength();
        LOG.log(Level.FINER,
                "Got length specification for following data: {0} bytes.",
                new Object[] { length });

        // read notification data
        final byte[] notificationData = new byte[length];
        final ByteBuffer notifDataBuffer = ByteBuffer
                .wrap(notificationData);
        notifDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        final int bytesRead = this.reader.read(notifDataBuffer);
        if (bytesRead != length) {
            throw new IOException("Received data length " + bytesRead
                    + " does not match the expected length " + length);
        }
        notifDataBuffer.rewind();

        LOG.fine("Received notification data. Decoding and returning.");

        // parse and return notification
        return Notification.parseFrom(notificationData);

    }

    @Override
    public void sendNotification(final Notification notification)
            throws IOException {

        if (!isActive()) {
            throw new IllegalStateException(
                    "Cannot read. Connection is not active.");
        }

        LOG.fine("Sending new notification.");

        final byte[] data = notification.toByteArray();

        // send data size
        final ByteBuffer sizeBuffer = ByteBuffer
                .allocateDirect(Protocol.DATA_SIZE_BYTES);
        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        sizeBuffer.asIntBuffer().put(data.length);
        this.writer.write(sizeBuffer);
        LOG.log(Level.FINER, "Sent out size specification for {0} bytes",
                data.length);

        // send real data
        this.writer.write(ByteBuffer.wrap(data));

        LOG.fine("Sending of notification succeeded");

    }

}
