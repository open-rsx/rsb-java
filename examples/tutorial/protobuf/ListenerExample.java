package tutorial.protobuf;
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
import rsb.Factory;
import rsb.Listener;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import tutorial.protobuf.ImageMessage.SimpleImage;

/**
 * An example that demonstrated how to receive events in java.
 * 
 * @author swrede
 */
public class ListenerExample {

	static AtomicInteger counter = new AtomicInteger(0);
	static Object l = new Object();

	public static void main(String[] args) throws Throwable {

		// Instantiate generic ProtocolBufferConverter with SimpleImage exemplar
		ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());
		
		// register converter for SimpleImage's
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);		
		
		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create a Listener instance on the specified scope that will receive
		// events and dispatches them asynchronously to all registered handlers
		Listener sub = factory.createListener(new Scope("/example/informer"));

		// activate the listener to be ready for work
		sub.activate();

		// add a DataHandler that is notified directly with the data grabbed
		// from the received event
		sub.addHandler(new AbstractDataHandler<SimpleImage>() {

			@Override
			public void handleEvent(SimpleImage e) {
				try {
					counter.getAndIncrement();
					System.out.println("SimpleImage Data received: size=" + e.getData().size() + ", event # "
								+ counter.get());
					if (counter.get() == 100) {
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
		return !(counter.get() != 100);
	}

}
