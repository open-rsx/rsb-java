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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import rsb.AbstractDataHandler;
import rsb.Factory;
import rsb.InitializeException;
import rsb.Listener;

/**
 * A basic example that demonstrated how to receive event payloads.
 * 
 * @author swrede
 */
public class DataListenerExample extends AbstractDataHandler<String> {

	private static final Logger LOG = Logger.getLogger(DataListenerExample.class.getName());
	
	static AtomicInteger counter = new AtomicInteger(0);
	static Object l = new Object();

	/**
	 * The actual callback that is notified upon arrival of events. In contrast 
	 * to an EventListener, here the event payload is passed to the callback.
	 */	
	@Override
	public void handleEvent(String data) {
		counter.getAndIncrement();
		if (counter.get() % 100 == 0) {
			LOG.info("Event #" + counter.get() + " received with payload: " + data);
		}
		if (counter.get() == 1000) {
			synchronized (l) {
				l.notifyAll();
			}
		}
	}	
	
	public static void main(String[] args) throws InterruptedException, InitializeException {

		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create a Listener instance on the specified scope that will receive
		// events and dispatches them asynchronously to all registered handlers
		Listener sub = factory.createListener("/example/informer");

		// activate the listener to be ready for work
		sub.activate();

		// add a DataHandler, here the DataListenerExample that is notified directly 
		// with the data extracted from the received event
		sub.addHandler(new DataListenerExample(), true);

		// wait that enough events are received
		while (!allEventsDelivered()) {
			synchronized (l) {
				l.wait();
				LOG.fine("Wake-Up!!!");
			}
		}		

		// as there is no explicit removal model in java, always manually
		// deactivate the listener if it is not needed anymore
		sub.deactivate();
	};

	private synchronized static boolean allEventsDelivered() {
		return !(counter.get() != 1000);
	}


}
