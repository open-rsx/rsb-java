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

import rsb.InitializeException;
import rsb.RSBEvent;
import rsb.Subscriber;
import rsb.event.RSBDataListener;
import rsb.event.RSBEventListener;
import rsb.naming.NotFoundException;
import rsb.transport.TransportFactory;

/**
 * @author swrede
 *
 */
public class SubscriberExample {
	
	// TODO move this to a test case
	static AtomicInteger counter1 = new AtomicInteger(0);
	static AtomicInteger counter2 = new AtomicInteger(0);
	static Object l = new Object();
	
	private synchronized static boolean allEventsDelivered() {		
		return !((counter1.get()!=1200) || (counter2.get()!=1200));
	}
	
	public static void main(String[] args) throws InitializeException, NotFoundException, InterruptedException {
		Subscriber sub = new Subscriber("rsb://example/informer","rsb://example/informer",TransportFactory.getInstance());
		sub.activate();
		sub.addListener(new RSBEventListener<RSBEvent>() {

			@Override
			public void handleEvent(RSBEvent e) {
				counter1.getAndIncrement();
				if (counter1.get()%100==0) {
				System.out.println("Event received: " + e.toString() + " # " + counter1);
				}
				if (counter1.get()==1200) {
					synchronized (l) {
						l.notifyAll();
					}
				}
			}
			
		});
		sub.addListener(new RSBDataListener<String>() {

			@Override
			public void handleEvent(String e) {
				try {
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				counter2.getAndIncrement();
				if (counter2.get()%100==0) {
					System.out.println("Data received: " + e + " event # " + counter2.get());
				}
				if (counter2.get()==1200) {
					synchronized (l) {
						l.notifyAll();
					}
				}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
		});		
		while (!allEventsDelivered()) {
			synchronized (l) {
				l.wait();
				System.out.println("Wake-Up!!!");
			}		
		}
		sub.deactivate();
	};
}
