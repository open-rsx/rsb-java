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

import rsb.Factory;
import rsb.Informer;
import rsb.Scope;

/**
 * An example how to use the {@link rsb.Informer} class to send events.
 * 
 * @author swrede
 */
public class InformerExample {

	public static void main(String[] args) throws Throwable {

		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// create an informer on scope "/exmaple/informer" to send event
		// notifications. This informer is capable of sending Strings.
		Informer<String> informer = factory.createInformer(new Scope(
				"/example/informer"));

		// activate the informer to be ready for work
		informer.activate();

		// send several events using a method that accepts the data and
		// automatically creates an appropriate event internally.
		for (int i = 0; i < 100; i++) {
			informer.send("<message val=\"Hello World!\" nr=\"" + i + "\"/>");
		}

		// as there is no explicit removal model in java, always manually
		// deactivate the informer if it is not needed anymore
		informer.deactivate();

	}

}
