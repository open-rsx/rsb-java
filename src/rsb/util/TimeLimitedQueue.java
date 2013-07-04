/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010,2011 CoR-Lab, Bielefeld University
 * Copyright (C) 011 David Klotz <david -at- sofaecke -dot- org>
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

    public TimeLimitedQueue(final int capacity, final long timeWindow,
            final TimeUnit unit) {
        super(capacity);
        this.timeWindow = TimeUnit.MICROSECONDS.convert(timeWindow, unit);
    }

    private synchronized void discardOldEvents(final long currentTime) {
        if (this.queue.isEmpty()) {
            return;
        }

        Event oldestEvent = this.queue.peek();
        long oldestTime = oldestEvent.getMetaData().getCreateTime();

        while (((currentTime - oldestTime) > this.timeWindow)
                && !this.queue.isEmpty()) {
            // Discard the oldest element
            this.queue.poll();

            // Get the age of the next oldest element
            oldestEvent = this.queue.peek();
            oldestTime = oldestEvent.getMetaData().getCreateTime();
        }
    }

    @Override
    public boolean add(final Event e) {
        // Discard events outside the time window.
        // TODO: Do we always want to use the create time?
        final long currentTime = e.getMetaData().getCreateTime();
        this.discardOldEvents(currentTime);

        return super.add(e);
    }

    // TODO: Shouldn't the "offer" methods also be overwritten in such a queue?
}
