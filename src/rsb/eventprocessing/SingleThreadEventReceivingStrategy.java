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
import java.util.concurrent.LinkedBlockingQueue;

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
    private final BlockingQueue<Event> events =
            new LinkedBlockingQueue<Event>();
    private final Set<Handler> handlers = Collections
            .synchronizedSet(new HashSet<Handler>());
    private DispatchThread thread;

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

        @Override
        public void run() {

            try {

                outer: while (!interrupted()) {

                    final Event eventToDispatch = this.events.take();

                    // match
                    // TODO blocks filter potentially a long time
                    synchronized (SingleThreadEventReceivingStrategy.this.filters) {
                        // CHECKSTYLE.OFF: LineLength - no way to convince
                        // eclipse to wrap this
                        for (final Filter filter : SingleThreadEventReceivingStrategy.this.filters) {
                            if (!filter.match(eventToDispatch)) {
                                continue outer;
                            }
                        }
                        // CHECKSTYLE.ON: LineLength
                    }

                    // dispatch
                    // TODO suboptimal locking. blocks handlers a very long time
                    synchronized (SingleThreadEventReceivingStrategy.this.handlers) {
                        // CHECKSTYLE.OFF: LineLength - no way to convince
                        // eclipse to wrap this
                        for (final Handler handler : SingleThreadEventReceivingStrategy.this.handlers) {
                            handler.internalNotify(eventToDispatch);
                        }
                        // CHECKSTYLE.ON: LineLength
                    }

                }

            } catch (final InterruptedException e) {
                return;
            }

        }

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
        this.handlers.add(handler);
    }

    @Override
    public void removeHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        this.handlers.remove(handler);
    }

    @Override
    public void activate() {
        synchronized (this) {
            if (this.thread != null) {
                throw new IllegalStateException("Already activated.");
            }
            this.thread = new DispatchThread(this.events);
            this.thread.start();
        }
    }

    @Override
    public void deactivate() throws InterruptedException {
        synchronized (this) {
            if (this.thread == null) {
                throw new IllegalStateException("Already deactivated.");
            }
            this.thread.interrupt();
            this.thread.join();
        }
    }

    @Override
    public boolean isActive() {
        return this.thread != null;
    }

}
