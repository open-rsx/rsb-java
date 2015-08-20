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
package rsb.eventprocessing;

import rsb.Handler;
import rsb.transport.InPushConnector;

/**
 * Instances of this class set up routes to receive events with asynchronous
 * notifications for new events.
 *
 * The methods {@link #handlerAdded(Handler, boolean)} and
 * {@link #handlerRemoved(Handler, boolean)} need to be callable before and
 * after calling {@link #activate()}.
 *
 * @author jwienke
 */
public interface PushInRouteConfigurator extends
        InRouteConfigurator<InPushConnector> {

    /**
     * Called in case a {@link Handler} was added to the participant for which
     * the route is being configured. This method should incorporate that
     * handler into the route.
     *
     * @param handler
     *            the added handler
     * @param wait
     *            if <code>true</code>, this method must wait with returning
     *            until the handler is fully active and can receive the next
     *            event. Otherwise it might return earlier.
     * @throws InterruptedException
     *             interrupted while waiting for the handler to be fully removed
     */
    void handlerAdded(Handler handler, boolean wait)
            throws InterruptedException;

    /**
     * Called in case a {@link Handler} was remove from the participant for
     * which the route is being configured. This method should remove that
     * handler from the route.
     *
     * @param handler
     *            the removed handler
     * @param wait
     *            if <code>true</code>, this method must wait with returning
     *            until the handler is fully removed and will not receive any
     *            more events.. Otherwise it might return earlier.
     * @return <code>true</code> if the handler was already available and is now
     *         removed, else <code>false</code>
     * @throws InterruptedException
     *             interrupted while waiting for the handler to be fully removed
     */
    boolean handlerRemoved(Handler handler, boolean wait)
            throws InterruptedException;

    /**
     * Defines the {@link EventReceivingStrategy} to use by the configurator.
     * Must be called directly after construction before any other method was
     * called.
     *
     * @param strategy
     *            the new strategy
     */
    void setEventReceivingStrategy(EventReceivingStrategy strategy);

}
