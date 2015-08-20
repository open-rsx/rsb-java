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

package rsb.util;

import java.util.concurrent.TimeUnit;

import rsb.Event;

/**
 * A time and space limited queue of RSB {@link Event}s that discards past
 * events older than a specified time limit. It will additionally discard old
 * events when reaching a set maximum capacity.
 *
 * @author dklotz
 */
public class TimeLimitedQueue extends LimitedQueue<Event> {

    private final long timeWindow;

    /**
     * Creates a new queue with the specified maximum capacity and time window
     * for keeping events in the queue.
     *
     * @param capacity
     *            maximum capacity before discarding events
     * @param timeWindow
     *            time to keep events in the specified unit
     * @param unit
     *            unit for the time window
     */
    public TimeLimitedQueue(final int capacity, final long timeWindow,
            final TimeUnit unit) {
        super(capacity);
        this.timeWindow = TimeUnit.MICROSECONDS.convert(timeWindow, unit);
    }

    private void discardOldEvents(final long currentTime) {
        synchronized (this) {
            if (getQueue().isEmpty()) {
                return;
            }

            Event oldestEvent = getQueue().peek();
            long oldestTime = oldestEvent.getMetaData().getCreateTime();

            while (currentTime - oldestTime > this.timeWindow
                    && !getQueue().isEmpty()) {
                // Discard the oldest element
                getQueue().poll();

                // Get the age of the next oldest element
                oldestEvent = getQueue().peek();
                oldestTime = oldestEvent.getMetaData().getCreateTime();
            }
        }
    }

    @Override
    public boolean add(final Event event) {
        // Discard events outside the time window.
        // TODO: Do we always want to use the create time?
        final long currentTime = event.getMetaData().getCreateTime();
        this.discardOldEvents(currentTime);

        return super.add(event);
    }

    // TODO: Shouldn't the "offer" methods also be overwritten in such a queue?
}
