package rsb.transport.socket;

import java.io.IOException;
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
    private SocketOptions options;

    public Socket getSocket() {
        return this.socket;
    }

    protected void setSocket(final Socket socket) {
        synchronized (this) {
            this.socket = socket;
        }
    }

    protected void setOptions(final SocketOptions options) {
        assert options != null;
        synchronized (this) {
            this.options = options;
        }
    }

    @Override
    public SocketOptions getOptions() {
        return this.options;
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
                this.socket.setTcpNoDelay(this.options.isTcpNoDelay());
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
     * Reads data from a {@link ReadableByteChannel} until a give buffer is
     * completely filled up.
     *
     * @param reader
     *            the channel to read from
     * @param buffer
     *            the buffer to fill
     * @throws IOException
     *             reading error or reader disconnected while reading
     */
    private static void readCompleteBuffer(final ReadableByteChannel reader,
            final ByteBuffer buffer) throws IOException {
        do {
            final int bytesRead = reader.read(buffer);
            if (bytesRead < 0) {
                throw new IOException("Socket connection error with negative "
                        + "return value for read.");
            }
        } while (buffer.position() < buffer.limit());
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
        readCompleteBuffer(this.reader, lengthBytes);
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
        final ByteBuffer notifDataBuffer = ByteBuffer.wrap(notificationData);
        notifDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        readCompleteBuffer(this.reader, notifDataBuffer);
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
                    "Cannot send. Connection is not active.");
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
