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
package rsb.eventprocessing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * An {@link EventReceivingStrategy} that uses a single thread for all handlers.
 *
 * @author jwienke
 */
public class SingleThreadEventReceivingStrategy implements
        EventReceivingStrategy {

    private final Set<Filter> filters = Collections
            .synchronizedSet(new HashSet<Filter>());
    private final BlockingQueue<Event> events = new SynchronousQueue<Event>();
    private final DispatchThread thread;

    /**
     * A thread that matches events and dispatches them to all handlers that are
     * registered in his internal set of handlers.
     *
     * @author jwienke
     */
    private class DispatchThread extends Thread {

        private final BlockingQueue<Event> events;

        private final Set<Handler> handlers = Collections
                .synchronizedSet(new HashSet<Handler>());

        public DispatchThread(final BlockingQueue<Event> events) {
            this.events = events;
        }

        public void addHandler(final Handler handler,
                @SuppressWarnings("unused") final boolean wait) {
            this.handlers.add(handler);
        }

        public void removeHandler(final Handler handler,
                @SuppressWarnings("unused") final boolean wait)
                throws InterruptedException {
            this.handlers.remove(handler);
        }

        @Override
        public void run() {

            try {

                outer: while (!interrupted()) {

                    final Event eventToDispatch = this.events.take();

                    // match
                    // TODO blocks filter potentially a long time
                    synchronized (SingleThreadEventReceivingStrategy.this.filters) {
                        for (final Filter filter : SingleThreadEventReceivingStrategy.this.filters) {
                            if (filter.transform(eventToDispatch) == null) {
                                continue outer;
                            }
                        }
                    }

                    // dispatch
                    // TODO suboptimal locking. blocks handlers a very long time
                    synchronized (this.handlers) {
                        for (final Handler handler : this.handlers) {
                            handler.internalNotify(eventToDispatch);
                        }
                    }

                }

            } catch (final InterruptedException e) {
                return;
            }

        }

    }

    public SingleThreadEventReceivingStrategy() {
        this.thread = new DispatchThread(this.events);
        this.thread.start();
    }

    @Override
    public void handle(final Event event) {
        try {
            this.events.put(event);
        } catch (final InterruptedException e) {
            // This must not happen
            assert false;
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFilter(final Filter filter) {
        this.filters.add(filter);
    }

    @Override
    public void removeFilter(final Filter filter) {
        this.filters.remove(filter);
    }

    @Override
    public void addHandler(final Handler handler, final boolean wait) {
        this.thread.addHandler(handler, wait);
    }

    @Override
    public void removeHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.thread.removeHandler(handler, wait);
    }

    @Override
    public void shutdownAndWait() throws InterruptedException {
        this.thread.interrupt();
        this.thread.join();
    }

}
