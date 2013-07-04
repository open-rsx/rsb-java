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

    /**
     * A thread that matches events and dispatches them to all handlers that are
     * registered in his internal set of handlers.
     * 
     * @author jwienke
     */
    private class DispatchThread extends Thread {

        private final BlockingQueue<Event> events;

        public DispatchThread(final BlockingQueue<Event> events) {
            this.events = events;
        }

        private final Set<Handler> handlers = Collections
                .synchronizedSet(new HashSet<Handler>());

        public void addHandler(final Handler handler, final boolean wait) {
            this.handlers.add(handler);
        }

        public void removeHandler(final Handler handler, final boolean wait)
                throws InterruptedException {
            this.handlers.remove(handler);
        }

        @Override
        public void run() {

            try {

                outer: while (!interrupted()) {

                    final Event e = this.events.take();

                    // match
                    // TODO blocks filter potentially a long time
                    synchronized (SingleThreadEventReceivingStrategy.this.filters) {
                        for (final Filter f : SingleThreadEventReceivingStrategy.this.filters) {
                            if (f.transform(e) == null) {
                                continue outer;
                            }
                        }
                    }

                    // dispatch
                    // TODO suboptimal locking. blocks handlers a very long time
                    synchronized (this.handlers) {
                        for (final Handler h : this.handlers) {
                            h.internalNotify(e);
                        }
                    }

                }

            } catch (final InterruptedException e) {
                return;
            }

        }

    }

    private final DispatchThread thread;

    public SingleThreadEventReceivingStrategy() {
        this.thread = new DispatchThread(this.events);
        this.thread.start();
    }

    @Override
    public void handle(final Event e) {
        try {
            this.events.put(e);
        } catch (final InterruptedException e1) {
            // This must not happen
            assert false;
            throw new RuntimeException(e1);
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
