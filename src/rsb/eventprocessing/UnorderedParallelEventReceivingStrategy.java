/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	final static Logger LOG = Logger.getLogger(UnorderedParallelEventReceivingStrategy.class
			.getName());

	private Set<Filter> filters = Collections
			.synchronizedSet(new HashSet<Filter>());
	private Map<Handler, Set<MatchAndDispatchTask>> handlerTasks = new HashMap<Handler, Set<MatchAndDispatchTask>>();

	public UnorderedParallelEventReceivingStrategy() {
		super(1, 1, 60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000));
		LOG.fine("Creating ThreadPool with size: 1 (1)");
		this.prestartAllCoreThreads();
	}

	public UnorderedParallelEventReceivingStrategy(int coreThreads,
			int maxThreads, int maxQueue) {
		super(coreThreads, maxThreads, 60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueue));
		LOG.fine("Creating ThreadPool with size: " + coreThreads + "("
				+ maxThreads + ") and queue size: " + maxQueue);
		this.prestartAllCoreThreads();
	}

	@Override
	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	@Override
	public void removeFilter(Filter filter) {
		filters.remove(filter);
	}

	@Override
	public void addHandler(Handler handler, boolean wait) {
		synchronized (handlerTasks) {
			handlerTasks.put(handler, new HashSet<MatchAndDispatchTask>());
		}
	}

	@Override
	public void removeHandler(Handler handler, boolean wait)
			throws InterruptedException {
		synchronized (handlerTasks) {
			if (wait && handlerTasks.containsKey(handler)) {
				while (!handlerTasks.get(handler).isEmpty()) {
					handlerTasks.wait();
				}
			}
			handlerTasks.remove(handler);
		}
	}

	@Override
	public void handle(Event event) {
		int count = 0;
		event.getMetaData().setDeliverTime(0);
		synchronized (handlerTasks) {
			for (Handler handler : handlerTasks.keySet()) {
				count++;
				MatchAndDispatchTask task = new MatchAndDispatchTask(handler,
						filters, event, handlerTasks);
				try {
					handlerTasks.get(handler).add(task);
					this.submit(task);
				} catch (RejectedExecutionException ex) {
					handlerTasks.get(handler).remove(task);
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
		synchronized (handlerTasks) {
			return handlerTasks.keySet();
		}
	}

}
