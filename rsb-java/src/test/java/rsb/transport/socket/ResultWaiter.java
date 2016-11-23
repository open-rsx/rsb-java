/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2016 CoR-Lab, Bielefeld University
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

import rsb.protocol.NotificationType.Notification;
import rsb.transport.socket.Bus.NotificationReceiver;

/**
 * Utility class implementing the {@link NotificationReceiver} interface to wait
 * for a a notification to arrive.
 *
 * @author jwienke
 *
 */
public class ResultWaiter implements NotificationReceiver {

    private Notification received = null;

    @Override
    public void handle(final Notification notification) {
        synchronized (this) {
            this.received = notification;
            this.notifyAll();
        }
    }

    /**
     * Waits for a {@link Notification}, returns the received one and resets the
     * state of this waiter.
     *
     * @return the received notification or <code>null</code> if none was
     *         received
     * @throws InterruptedException
     *             interrupted
     */
    public Notification waitForResult() throws InterruptedException {
        synchronized (this) {

            final long waitTime = 25000;
            final long waitStart = System.currentTimeMillis();
            while (this.received == null
                    && System.currentTimeMillis() < waitStart + waitTime) {
                this.wait(waitTime);
            }

            final Notification received = this.received;
            this.received = null;

            return received;

        }
    }

}
