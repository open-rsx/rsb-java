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
	
	public static void main(String[] args) throws InitializeException, NotFoundException {
		Subscriber sub = new Subscriber("rsb://example/informer","rsb://example/informer",TransportFactory.getInstance());
		sub.activate();
		sub.addListener(new RSBEventListener<RSBEvent>() {

			@Override
			public void handleEvent(RSBEvent e) {
				System.out.println("Event received: " + e.toString());
			}
			
		});
		sub.addListener(new RSBDataListener<String>() {

			@Override
			public void handleEvent(String e) {
				System.out.println("Event received: " + e);
			}
			
		});		
	};
}
