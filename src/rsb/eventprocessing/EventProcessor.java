/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import rsb.Event;
import rsb.Handler;
import rsb.filter.Filter;

/**
 * @author swrede
 * 
 */
public class EventProcessor extends ThreadPoolExecutor {

	// TODO add support for single threaded, queue receive, pull style, lazy
	// evaluation
	// TODO refactor to use ThreadPoolExecutor as delegate, not as derived class

	Logger log = Logger.getLogger(EventProcessor.class.getName());

	ArrayList<Filter> filters = new ArrayList<Filter>();
	ArrayList<Handler<Event>> handlers = new ArrayList<Handler<Event>>();

	public EventProcessor() {
		super(1, 1, 60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(1000));
		log.fine("Creating ThreadPool with size: 1 (1)");
		this.prestartAllCoreThreads();
	}

	public EventProcessor(int coreThreads, int maxThreads, int maxQueue) {
		super(coreThreads, maxThreads, 60, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(maxQueue));
		log.fine("Creating ThreadPool with size: " + coreThreads + "("
				+ maxThreads + ") and queue size: " + maxQueue);
		this.prestartAllCoreThreads();
	}

	public void addFilter(Filter filter) {
		filters.add(filter);
	}

	public void removeFilter(Filter filter) {
		filters.remove(filter);
	}

	public void addHandler(Handler<Event> handler) {
		handlers.add(handler);
	}

	public void removeHandler(Handler<Event> handler) {
		handlers.remove(handler);
	}

	public void fire(Event event) {
		int count = 0;
		for (Handler<Event> handler : handlers) {
			count++;
			try {
				this.submit(new MatchAndDispatchTask(handler, filters, event));
			} catch (RejectedExecutionException ex) {
				log.log(Level.SEVERE,
						"ExecutorService rejected event matching", ex);
			}

		}
		log.fine("Dispatched event to " + count + " subscriptions");
	}

	/**
	 * @throws InterruptedException
	 *             thrown if waiting for shutdown was interrupted.
	 */
	public void waitForShutdown() throws InterruptedException {
		this.shutdown();
		this.awaitTermination(10, TimeUnit.SECONDS);
	}
}
