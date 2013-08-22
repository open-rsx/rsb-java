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

import java.io.IOException;

import rsb.Activatable;
import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * Interface for connections to the socket based transport.
 *
 * @author jwienke
 */
public interface BusConnection extends Activatable {

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

    /**
     * This method starts and orderly shutdown of the connection. It needs to be
     * called before {@link #deactivate()} for this procedure to succeed. Should
     * be callable multiple times without raising an error. Calls after the
     * first are usually ignored.
     *
     * @throws IOException
     *             error indicating the shutdown due to socket writing problems
     */
    void shutdown() throws IOException;

    /**
     * Indicates whether the connection is currently performing an active
     * shutdown as a consequence of calling {@link #shutdown()}.
     *
     * @return <code>true</code> in case of active shutdown, else
     *         <code>false</code>
     */
    boolean isActiveShutdown();

}
