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

import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * A {@link Bus} implementation which acts as a client to an existing server
 * implementation. This means that new notifications are only sent to local
 * receivers as well as to a single {@link BusConnection} connecting with the
 * server.
 *
 * @author swrede
 * @author jwienke
 */
public class BusClient extends Bus {

    private static final Logger LOG = Logger.getLogger(BusClient.class
            .getName());

    private BusClientConnection connection;

    public BusClient(final SocketOptions options) {
        super(options);
    }

    @Override
    public void activate() throws RSBException {

        LOG.log(Level.FINE, "Trying to activate BusClient");

        synchronized (this) {

            if (isActive()) {
                throw new IllegalStateException("BusClient is already active.");
            }

            this.connection = new BusClientConnection(this.getSocketOptions());
            this.connection.activate();
            this.connection.handshake();

            addConnection(this.connection);

        }

    }

    @Override
    public void deactivate() throws RSBException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("BusClient is not active.");
            }

            this.connection.deactivate();
            removeConnection(this.connection);
            this.connection = null;

        }

    }

    @Override
    public boolean isActive() {
        return this.connection != null;
    }

    @Override
    public void handleIncoming(final Notification notification)
            throws RSBException {
        handleLocally(notification);
    }

}
