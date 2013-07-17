/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * An {@link EventReceivingStrategy} that dispatches {@link Event}s using a
 * thread pool but without any ordering guarantees.
 *
 * @author swrede
 */
public class UnorderedParallelEventReceivingStrategy extends ThreadPoolExecutor
        implements EventReceivingStrategy {

    // TODO add support for single threaded, queue receive, pull style, lazy
    // evaluation
    // TODO refactor to use ThreadPoolExecutor as delegate, not as derived class

    final static Logger LOG = Logger
            .getLogger(UnorderedParallelEventReceivingStrategy.class.getName());

    private final Set<Filter> filters = Collections
            .synchronizedSet(new HashSet<Filter>());
    private final Map<Handler, Set<MatchAndDispatchTask>> handlerTasks = new HashMap<Handler, Set<MatchAndDispatchTask>>();

    public UnorderedParallelEventReceivingStrategy() {
        super(1, 1, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1000));
        LOG.fine("Creating ThreadPool with size: 1 (1)");
        this.prestartAllCoreThreads();
    }

    public UnorderedParallelEventReceivingStrategy(final int coreThreads,
            final int maxThreads, final int maxQueue) {
        super(coreThreads, maxThreads, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(maxQueue));
        LOG.fine("Creating ThreadPool with size: " + coreThreads + "("
                + maxThreads + ") and queue size: " + maxQueue);
        this.prestartAllCoreThreads();
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
        synchronized (this.handlerTasks) {
            this.handlerTasks.put(handler, new HashSet<MatchAndDispatchTask>());
        }
    }

    @Override
    public void removeHandler(final Handler handler, final boolean wait)
            throws InterruptedException {
        synchronized (this.handlerTasks) {
            if (wait && this.handlerTasks.containsKey(handler)) {
                while (!this.handlerTasks.get(handler).isEmpty()) {
                    this.handlerTasks.wait();
                }
            }
            this.handlerTasks.remove(handler);
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void handle(final Event event) {
        int count = 0;
        event.getMetaData().setDeliverTime(0);
        synchronized (this.handlerTasks) {
            for (final Handler handler : this.handlerTasks.keySet()) {
                count++;
                final MatchAndDispatchTask task = new MatchAndDispatchTask(
                        handler, this.filters, event, this.handlerTasks);
                try {
                    this.handlerTasks.get(handler).add(task);
                    this.submit(task);
                } catch (final RejectedExecutionException ex) {
                    this.handlerTasks.get(handler).remove(task);
                    LOG.log(Level.SEVERE,
                            "ExecutorService rejected event matching", ex);
                }

            }
        }
        LOG.fine("Dispatched event to " + count + " subscriptions");
    }

    /**
     * @throws InterruptedException
     *             thrown if waiting for shutdown was interrupted.
     */
    @Override
    public void shutdownAndWait() throws InterruptedException {
        this.shutdown();
        this.awaitTermination(10, TimeUnit.SECONDS);
    }

    Set<Handler> getHandlers() {
        synchronized (this.handlerTasks) {
            return this.handlerTasks.keySet();
        }
    }

}
