package protobuf;
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
import rsb.converter.ProtocolBufferConverter;
import rsb.protocol.Protocol;

/**
 * An example how to use the {@link rsb.Informer} class to send events.
 * 
 * @author swrede
 */
public class InformerExample {

	public static void main(String[] args) throws Throwable {

//		   shared_ptr<ProtocolBufferConverter<SimpleImage> > converter(
//		            new ProtocolBufferConverter<SimpleImage> ());
//		    stringConverterRepository()->registerConverter(converter);
//
//		    // Create an informer which has the SimpleImage protocol buffer
//		    // message as its data type.
//		    Informer<SimpleImage>::Ptr informer =
//		            Factory::getInstance().createInformer<SimpleImage> (
//		                    Scope("/tutorial/converter"));
//
//		    // Create and publish an instance of SimpleImage. To see the
//		    // event, you can, for example use the RSB logger utility or the
//		    // receiver program in this directory.
//		    Informer<SimpleImage>::DataPtr data(new SimpleImage());
//		    data->set_width(10);
//		    data->set_height(10);
//		    data->set_data(new char[100], 100);
//		    informer->publish(data);				
		
		ProtocolBufferConverter<Protocol.Notification> converter = new ProtocolBufferConverter<Protocol.Notification>(Protocol.Notification.getDefaultInstance());
		
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
