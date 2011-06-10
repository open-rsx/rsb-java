/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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

	private Set<Filter> filters = Collections
			.synchronizedSet(new HashSet<Filter>());
	private BlockingQueue<Event> events = new SynchronousQueue<Event>();

	/**
	 * A thread that matches events and dispatches them to all handlers that are
	 * registered in his internal set of handlers.
	 * 
	 * @author jwienke
	 */
	private class DispatchThread extends Thread {

		private BlockingQueue<Event> events;

		public DispatchThread(BlockingQueue<Event> events) {
			this.events = events;
		}

		private Set<Handler> handlers = Collections
				.synchronizedSet(new HashSet<Handler>());

		public void addHandler(Handler handler, boolean wait) {
			handlers.add(handler);
		}

		public void removeHandler(Handler handler, boolean wait)
				throws InterruptedException {
			handlers.remove(handler);
		}

		@Override
		public void run() {

			try {

				while (!interrupted()) {

					Event e = events.take();

					// match
					// TODO blocks filter potentially a long time
					synchronized (filters) {
						for (Filter f : filters) {
							if (f.transform(e) == null) {
								continue;
							}
						}
					}

					// dispatch
					// TODO suboptimal locking. blocks handlers a very long time
					synchronized (handlers) {
						for (Handler h : handlers) {
							h.internalNotify(e);
						}
					}

				}

			} catch (InterruptedException e) {
				return;
			}

		}

	}

	private DispatchThread thread;

	public SingleThreadEventReceivingStrategy() {
		thread = new DispatchThread(events);
		thread.start();
	}

	@Override
	public void handle(Event e) {
		try {
			events.put(e);
		} catch (InterruptedException e1) {
			// This must not happen
			assert false;
			throw new RuntimeException(e1);
		}
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
		thread.addHandler(handler, wait);
	}

	@Override
	public void removeHandler(Handler handler, boolean wait)
			throws InterruptedException {
		thread.removeHandler(handler, wait);
	}

	@Override
	public void shutdownAndWait() throws InterruptedException {
		thread.interrupt();
		thread.join();
	}

}
