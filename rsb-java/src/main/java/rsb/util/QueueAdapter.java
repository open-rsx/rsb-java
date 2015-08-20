/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import rsb.AbstractDataHandler;

/**
 * Synchronized queue implementing the rsb.DataHandler interface. Can be
 * directly registered as handler in rsb.Listener instance and used for
 * receiving and storing dispatched events.
 *
 * @author swrede
 * @author dklotz
 * @param <DataType>
 *            type of data to be handled
 */
public class QueueAdapter<DataType> extends AbstractDataHandler<DataType> {

    private BlockingQueue<DataType> queue;

    /**
     * Creates an adapter with a preset unlimited queue inside.
     */
    public QueueAdapter() {
        this.queue = new LinkedBlockingDeque<DataType>();
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
    public QueueAdapter(final int capacity, final boolean discardOldest) {
        if (discardOldest) {
            this.queue = new LimitedQueue<DataType>(capacity);
        } else {
            this.queue = new LinkedBlockingDeque<DataType>(capacity);
        }
    }

    /**
     * Creates an adapter that uses the specified queue as target for new data.
     *
     * @param queue
     *            queue for new data
     */
    public QueueAdapter(final BlockingQueue<DataType> queue) {
        this.queue = queue;
    }

    @Override
    public void handleEvent(final DataType data) {
        this.queue.add(data);
    }

    /**
     * Returns the queue instance used for new data.
     *
     * @return queue instance
     */
    public BlockingQueue<DataType> getQueue() {
        return this.queue;
    }
}
