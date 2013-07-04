/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
 * Copyright (C) 2011 David Klotz <david -at- sofaecke -dot- org>
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
 * ============================================================
 */

package rsb.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import rsb.AbstractEventHandler;
import rsb.Event;

/**
 * An adapter similar to the {@link QueueAdapter} that provides access to a
 * queue of RSB {@link Event}s instead of directly to the payload data. It can
 * also be directly registered as handler in an rsb.Listener instance and used
 * for receiving and storing dispatched events.
 * 
 * @author dklotz
 */
public class EventQueueAdapter extends AbstractEventHandler {

    BlockingQueue<Event> queue;

    /**
     * Creates an adapter with a preset unlimited queue inside.
     */
    public EventQueueAdapter() {
        this.queue = new LinkedBlockingDeque<Event>();
    }

    /**
     * Creates an adapter with a preset queue inside that is limited to
     * <code>capacity</code> elements.
     * 
     * @param capacity
     *            capacity of the internal queue
     * @param discardOldest
     *            if <code>true</code>, remove older events if the queue is
     *            full, otherwise block until space is available on inserts
     */
    public EventQueueAdapter(final int capacity, final boolean discardOldest) {
        if (!discardOldest) {
            this.queue = new LinkedBlockingDeque<Event>(capacity);
        } else {
            this.queue = new LimitedQueue<Event>(capacity);
        }
    }

    /**
     * Creates an adapter with the given queue implementation.
     * 
     * @param queue
     *            The queue this adapter should fill.
     */
    public EventQueueAdapter(final BlockingQueue<Event> queue) {
        this.queue = queue;
    }

    @Override
    public void handleEvent(final Event event) {
        this.queue.add(event);
    }

    /**
     * Provides access to the queue filled by this queue adapter.
     * 
     * @return The current with the events received by this adapter.
     */
    public BlockingQueue<Event> getQueue() {
        return this.queue;
    }
}
