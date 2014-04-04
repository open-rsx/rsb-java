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

import rsb.RSBException;
import rsb.protocol.NotificationType.Notification;

/**
 * A reference counting decorator for {@link Bus} instances. The wrapped
 * instance is activated only to the first call to {@link #activate()} and
 * deactivated only when as many {@link #deactivate()} calls are received as
 * have been activate calls received.
 *
 * @author jwienke
 */
public class RefCountingBus implements Bus {

    private final Bus bus;
    private int count = 0;
    private final DeactivationHandler deactivationHandler;

    /**
     * A handler that will be called once the underlying bus will really be
     * deactivated.
     *
     * @author jwienke
     */
    public interface DeactivationHandler {

        /**
         * Called on deactivation of a reference-counted bus instance.
         *
         * @param bus
         *            the bus being deactivated
         */
        void deactivated(final RefCountingBus bus);

    }

    /**
     * Constructor.
     *
     * @param bus
     *            bus to manage
     * @param handler
     *            handler to be called on deactivation of the bus
     */
    public RefCountingBus(final Bus bus, final DeactivationHandler handler) {
        this.bus = bus;
        this.deactivationHandler = handler;
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this.bus) {
            if (this.count == 0) {
                this.bus.activate();
            }
            ++this.count;
        }
    }

    @Override
    public boolean isActive() {
        return this.bus.isActive();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this.bus) {
            if (this.count == 0) {
                throw new IllegalStateException(
                        "Received more deactivation calls than activation calls.");
            }
            --this.count;
            if (this.count == 0) {
                this.bus.deactivate();
                this.deactivationHandler.deactivated(this);
            }
        }
    }

    @Override
    public SocketOptions getSocketOptions() {
        return this.bus.getSocketOptions();
    }

    @Override
    public void handleOutgoing(final Notification notification)
            throws RSBException {
        this.bus.handleOutgoing(notification);
    }

    @Override
    public void addNotificationReceiver(final NotificationReceiver receiver) {
        this.bus.addNotificationReceiver(receiver);
    }

    @Override
    public void removeNotificationReceiver(final NotificationReceiver receiver) {
        this.bus.removeNotificationReceiver(receiver);
    }

    /**
     * Returns the underlying bus that is handled with reference counting. Do
     * not call {@link Bus#activate()} or {@link Bus#deactivate()} on this
     * instance!
     *
     * @return the managed bus
     */
    public Bus getContainedBus() {
        return this.bus;
    }

}
