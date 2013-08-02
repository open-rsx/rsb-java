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
import java.util.logging.Logger;

import rsb.RSBException;

/**
 * Instances of this class implement connections to a socket-based bus in the
 * form of a server.
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
 */
public class BusServerConnection extends BusConnectionBase {

    private static final Logger LOG = Logger
            .getLogger(BusServerConnection.class.getName());

    /**
     * Constructs a new server.
     *
     * @param socket
     *            socket to use for the server side. This is usually the result
     *            of calling {@link java.net.ServerSocket#accept()}.
     * @param tcpNoDelay
     *            if <code>true</code>, configure the socket to use tcp no delay
     */
    public BusServerConnection(final Socket socket, final boolean tcpNoDelay) {
        setSocket(socket);
        setOptions(new SocketOptions(socket.getLocalAddress(),
                socket.getPort(), tcpNoDelay));
    }

    @Override
    public void handshake() throws RSBException {

        LOG.fine("Performing handshake.");

        try {
            final ByteBuffer handshakeBytes =
                    ByteBuffer.allocateDirect(Protocol.HANDSHAKE_BYTES);
            handshakeBytes.asIntBuffer().put(Protocol.HANDSHAKE_DATA);
            getWriter().write(handshakeBytes);
        } catch (final IOException e) {
            throw new RSBException(
                    "Unable to send handshake data to new client.", e);
        }

        LOG.fine("Handshake complete");

    }

}
