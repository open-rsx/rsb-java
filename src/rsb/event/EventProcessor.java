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
package rsb.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import rsb.RSBEvent;
import rsb.filter.Subscription;

/**
 * @author swrede
 *
 */
public class EventProcessor extends ThreadPoolExecutor {
	
	// TODO add support for single threaded, queue receive, pull style, lazy evaluation
	// TODO refactor to use ThreadPoolExecutor as delegate, not as derived class
	
	Logger log = Logger.getLogger(EventProcessor.class.getName());
	ConcurrentLinkedQueue<Subscription> subscriptions = new ConcurrentLinkedQueue<Subscription>();
	
	public EventProcessor() {
		super( 1, 1, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100) );
		log.info("Creating ThreadPool with size: 1 (1)");
		this.prestartAllCoreThreads();
	}
		
	public EventProcessor(int coreThreads, int maxThreads, int maxQueue) {
		super( coreThreads, maxThreads, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(maxQueue) );
		
		//Properties.getInstance().getPropertyAsInt("XCF.ThreadPool.Size"),
		//Properties.getInstance().getPropertyAsInt("XCF.ThreadPool.SizeMax"),
		//5,50, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10)));
		//Properties.getInstance().getPropertyAsInt("XCF.Events.DispatchQueueSize")));
		log.info("Creating ThreadPool with size: " + coreThreads + "(" + maxThreads + ")");
		this.prestartAllCoreThreads();
	}
	
	public void addSubscription(Subscription s) {
		subscriptions.add(s);
	}
	
	public void removeSubscription(Subscription s) {
		subscriptions.remove(s);
	}
	
	public void fire(RSBEvent e) {		
		for (Subscription s: subscriptions) {
			this.submit(new MatchAndDispatchTask(s,e));
		}		
		// Future<Boolean> result = this.submit( new MatchAndDispatchTask(s, e) );
	}
	
	public void waitForShutdown() {
		this.shutdown();
		try {
			this.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
