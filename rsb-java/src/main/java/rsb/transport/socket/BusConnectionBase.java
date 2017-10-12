/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.AbstractActivatable;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * Utility base class for the implementation of the {@link BusConnection}
 * interface.
 *
 * Subclasses need to use this object for synchronization.
 *
 * Subclasses need to call {@link #setSocket(Socket)} with a valid socket before
 * {@link #activate()} will be called. {@link #activate()} will automatically
 * call {@link #handshake()}.
 *
 * @author jwienke
 */
public abstract class BusConnectionBase extends AbstractActivatable
                                        implements BusConnection {

    private static final Logger LOG =
            Logger.getLogger(BusConnectionBase.class.getName());

    /**
     * Time in seconds to wait for the submission pool to terminate when
     * deactivating.
     */
    private static final int POOL_SHUTDOWN_TIMEOUT_SEC = 10;

    private Socket socket;
    private ReadableByteChannel reader;
    private WritableByteChannel writer;
    private SocketOptions options;
    private boolean activeShutdown = false;

    /**
     * A thread pool (with a single thread) used to submit notifications to the
     * wire. This is used to decouple the sending thread from the client's
     * publish thread because otherwise, an interrupt set on the client thread
     * might close the sending socket (default java behavior).
     */
    private ExecutorService submissionPool;

    /**
     * Performs the handshake step of the protocol.
     *
     * @throws RSBException
     *             error during handshake
     */
    protected abstract void handshake() throws RSBException;

    /**
     * Returns the contained socket instance.
     *
     * @return socket instance or <code>null</code> if not set yet.
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Sets the internal socket instance.
     *
     * @param socket
     *            new socket instance
     */
    protected void setSocket(final Socket socket) {
        synchronized (this) {
            this.socket = socket;
        }
    }

    /**
     * Sets the options for the socket to hold in this instance.
     *
     * @param options
     *            socket options to hold
     */
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

    /**
     * Returns the reader for the internal socket.
     *
     * @return reader instance or <code>null</code> if called before
     *         {@link #activate()}
     */
    protected ReadableByteChannel getReader() {
        return this.reader;
    }

    /**
     * Returns the writer for the internal socket.
     *
     * @return writer instance or <code>null</code> if called before
     *         {@link #activate()}
     */
    protected WritableByteChannel getWriter() {
        return this.writer;
    }

    @Override
    public void activate() throws RSBException {
        LOG.finer("Activating connection");

        synchronized (this) {
            assert this.socket != null;
            try {
                this.socket.setTcpNoDelay(this.options.isTcpNoDelay());
                this.reader = Channels.newChannel(getSocket().getInputStream());
                this.writer =
                        Channels.newChannel(getSocket().getOutputStream());
            } catch (final IOException e) {
                this.reader = null;
                this.writer = null;
                throw new RSBException(e);
            }
            this.handshake();
            this.submissionPool = Executors.newSingleThreadExecutor();
        }

    }

    @Override
    public void shutdown() throws IOException {
        LOG.finest("Shutdown called");
        synchronized (this) {
            if (!this.activeShutdown) {
                this.socket.shutdownOutput();
                this.activeShutdown = true;
            }
        }
    }

    @Override
    public boolean isActiveShutdown() {
        return this.activeShutdown;
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        LOG.finer("Deactivating connection");

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Connection is not active.");
            }

            this.submissionPool.shutdown();
            this.submissionPool.awaitTermination(POOL_SHUTDOWN_TIMEOUT_SEC,
                    TimeUnit.SECONDS);

            try {
                getSocket().close();
            } catch (final IOException e) {
                LOG.log(Level.WARNING,
                        "Exception during deactivation. "
                                + "Ignoring this exception and doing so "
                                + "as if nothing happened.",
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
            if (bytesRead == -1) {
                throw new EOFException();
            } else if (bytesRead < 0) {
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
     *             error while reading from the socket
     */
    protected int readLength() throws IOException {
        assert isActive();
        final ByteBuffer lengthBytes =
                ByteBuffer.allocateDirect(Protocol.DATA_SIZE_BYTES);
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

        final byte[] data = notification.toByteArray();
        LOG.log(Level.FINE, "Sending new notification of size {0}",
                data.length);

        // send data size
        final ByteBuffer sizeBuffer =
                ByteBuffer.allocate(Protocol.DATA_SIZE_BYTES);
        sizeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        sizeBuffer.asIntBuffer().put(data.length);

        synchronized (this) {

            if (isActiveShutdown()) {
                LOG.log(Level.FINE,
                        "Not sending notification {0} "
                                + "since we are already in shutdown.",
                        notification);
                return;
            }

            if (!isActive()) {
                LOG.log(Level.FINE,
                        "Not sending notification {0} on "
                                + "this connection because it is not active.",
                        notification);
                return;
            }

            // use the submission pool for write operations to ensure that a
            // potentially existing interrupt state will not close the socket.
            try {
                this.submissionPool.submit(new Callable<Void>() {

                    @Override
                    // Interface requirement
                    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
                    public Void call() throws Exception {
                        BusConnectionBase.this.writer.write(sizeBuffer);
                        LOG.log(Level.FINER,
                                "Sent out size specification for {0} bytes",
                                data.length);

                        // send real data
                        BusConnectionBase.this.writer
                                .write(ByteBuffer.wrap(data));
                        return null;
                    }

                }).get();
            } catch (final ExecutionException e) {
                throw new IOException(e);
            } catch (final InterruptedException e) {
                // restore interrupted state for outer thread
                // cf. http://www.ibm.com/developerworks/library/j-jtp05236/
                Thread.currentThread().interrupt();
            }

        }

        LOG.fine("Sending of notification succeeded");

    }

}
