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

import rsb.AbstractDataHandler;
import rsb.EventHandler;
import rsb.Factory;
import rsb.Event;
import rsb.Scope;
import rsb.Listener;

/**
 * An example that demonstrated how to receive events in java.
 * 
 * @author swrede
 */
public class ListenerExample {

	static AtomicInteger counter1 = new AtomicInteger(0);
	static AtomicInteger counter2 = new AtomicInteger(0);
	static Object l = new Object();

	public static void main(String[] args) throws Throwable {

		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create a Listener instance on the specified scope that will receive
		// events and dispatches them asynchronously to all registered handlers
		Listener sub = factory.createListener(new Scope("/example/informer"));

		// activate the listener to be ready for work
		sub.activate();

		// add an EventHandler that will receive complete Event instances
		// whenever they are received
		sub.addHandler(new EventHandler() {

			@Override
			public void handleEvent(Event e) {
				counter1.getAndIncrement();
				if (counter1.get() % 100 == 0) {
					System.out.println("Event received: " + e.toString()
							+ " # " + counter1);
				}
				if (counter1.get() == 1200) {
					synchronized (l) {
						l.notifyAll();
					}
				}
			}

		}, true);

		// add a DataHandler that is notified directly with the data grabbed
		// from the received event
		sub.addHandler(new AbstractDataHandler<String>() {

			@Override
			public void handleEvent(String e) {
				try {
					counter2.getAndIncrement();
					if (counter2.get() % 100 == 0) {
						System.out.println("Data received: " + e + " event # "
								+ counter2.get());
					}
					if (counter2.get() == 1200) {
						synchronized (l) {
							l.notifyAll();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}, true);

		// wait that enough events are received
		while (!allEventsDelivered()) {
			synchronized (l) {
				l.wait();
				System.out.println("Wake-Up!!!");
			}
		}

		// as there is no explicit removal model in java, always manually
		// deactivate the listener if it is not needed anymore
		sub.deactivate();
	};

	private synchronized static boolean allEventsDelivered() {
		return !((counter1.get() != 1200) || (counter2.get() != 1200));
	}

}
