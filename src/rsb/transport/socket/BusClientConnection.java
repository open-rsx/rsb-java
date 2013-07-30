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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;

/**
 * Instances of this class implement connections to a socket-based bus in the
 * form of a client.
 *
 * The basic operations provided by this class are receiving an event
 * notifications by calling receiveNotification and submitting an event to the
 * bus by calling sendNotification. This class implements the fundamental RSB
 * protocol for socket connections, e.g., the basic handshaking and the
 * encoding/decoding of data packages for event notifications.
 *
 * @see <a
 *      href="http://docs.cor-lab.de/rsb-manual/trunk/html/specification-socket.html">RSB
 *      Specification for Socket Transport</a>
 *
 * @author swrede
 * @author jwienke
 */
// TODO nodelay flag
public class BusClientConnection extends BusConnectionBase {

    private static final Logger LOG = Logger
            .getLogger(BusClientConnection.class.getName());

    public BusClientConnection(final SocketOptions options) {
        this.setOptions(options);
    }

    @Override
    public void activate() throws RSBException {
        try {
            this.setSocket(new Socket(getOptions().getAddress(), getOptions()
                    .getPort()));
            super.activate();
        } catch (final IOException e) {
            throw new RSBException("Unable to create client socket for "
                    + getOptions(), e);
        }
    }

    @Override
    public void deactivate() throws RSBException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Connection is not active.");
            }

            try {
                getSocket().close();
            } catch (final IOException e) {
                LOG.log(Level.WARNING,
                        "Exception during deactivation. "
                                + "Ignoring this exception and doing so as if nothing happened.",
                        e);
            }

            super.deactivate();

        }

    }

    /**
     * Perform simple handshake as specified in RSB socket protocol.
     *
     * @throws RSBException
     */
    @Override
    public void handshake() throws RSBException {

        LOG.fine("Performing handshake as client.");

        try {

            // read handshake reply
            final ByteBuffer handshakeReplyBuffer = ByteBuffer
                    .allocateDirect(Protocol.HANDSHAKE_BYTES);
            handshakeReplyBuffer.order(ByteOrder.LITTLE_ENDIAN);
            final int bytesRead = getReader().read(handshakeReplyBuffer);
            LOG.log(Level.FINER, "Received {0} handshake bytes",
                    new Object[] { bytesRead });
            if (bytesRead != handshakeReplyBuffer.capacity()) {
                throw new RSBException(
                        "Handshake reply too short. Only received " + bytesRead
                                + " bytes instead of 4.");
            }
            handshakeReplyBuffer.rewind();

            // verify handshake reply
            if (Protocol.HANDSHAKE_DATA != handshakeReplyBuffer.getInt()) {
                throw new RSBException(
                        "RSB Handshake failed in SocketTransport at "
                                + getOptions()
                                + ". Received bytes sequence is not expected.");
            }

            LOG.fine("Handshake successfull");

        } catch (final IOException e) {
            throw new RSBException(e);
        }

    }

}
