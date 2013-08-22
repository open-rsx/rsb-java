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

import rsb.Activatable;
import rsb.RSBException;
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
