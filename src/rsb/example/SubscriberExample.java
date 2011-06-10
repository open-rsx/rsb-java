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
package rsb.example;

import java.util.concurrent.atomic.AtomicInteger;

import rsb.DataHandler;
import rsb.EventHandler;
import rsb.Factory;
import rsb.InitializeException;
import rsb.Event;
import rsb.Scope;
import rsb.Listener;
import rsb.naming.NotFoundException;

/**
 * @author swrede
 * 
 */
public class SubscriberExample {

	static AtomicInteger counter1 = new AtomicInteger(0);
	static AtomicInteger counter2 = new AtomicInteger(0);
	static Object l = new Object();

	private synchronized static boolean allEventsDelivered() {
		return !((counter1.get() != 1200) || (counter2.get() != 1200));
	}

	public static void main(String[] args) throws InitializeException,
			NotFoundException, InterruptedException {
		Listener sub = Factory.getInstance().createListener(
				new Scope("/example/informer"));
		sub.activate();
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
		sub.addHandler(new DataHandler<String>() {

			@Override
			public void handleEvent(String e) {
				try {
					// try {
					// Thread.sleep(1);
					// } catch (InterruptedException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
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
		while (!allEventsDelivered()) {
			synchronized (l) {
				l.wait();
				System.out.println("Wake-Up!!!");
			}
		}
		sub.deactivate();
	};
}
