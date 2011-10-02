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

import rsb.AbstractEventHandler;
import rsb.Factory;
import rsb.Event;
import rsb.Listener;

/**
 * A basic example that demonstrated how to receive events.
 * 
 * @author swrede
 */
public class EventListenerExample extends AbstractEventHandler {

	private static final Logger LOG = Logger.getLogger(EventListenerExample.class.getName());
	
	static AtomicInteger counter = new AtomicInteger(0);
	static Object l = new Object();

	/**
	 * The actual callback that is notified upon arrival of events. In contrast to
	 * a DataListener, an Event object is passed to the callback which in addition
	 * to the payload, also provides access to the Event meta data.
	 */
	@Override
	public void handleEvent(Event event) {
		counter.getAndIncrement();
		if (counter.get() % 100 == 0) {
			LOG.info("Event #" + counter.get() + " received with payload: " + event.toString());
		}
		if (counter.get() == 1000) {
			synchronized (l) {
				l.notifyAll();
			}
		}
	}	
	
	public static void main(String[] args) throws Throwable {

		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create a Listener instance on the specified scope that will receive
		// events and dispatches them asynchronously to all registered handlers
		Listener sub = factory.createListener("/example/informer");

		// activate the listener to be ready for work
		sub.activate();

		// add an EventHandler that will receive complete Event instances
		// whenever they are received
		sub.addHandler(new EventListenerExample(), true);

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
