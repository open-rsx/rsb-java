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

import com.google.protobuf.ByteString;

import rsb.Factory;
import rsb.Informer;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import tutorial.protobuf.ImageMessage.SimpleImage;
import tutorial.protobuf.ImageMessage.SimpleImage.Builder;

/**
 * An example how to use the {@link rsb.Informer} class to send events.
 * 
 * @author swrede
 */
public class InformerExample {

	public static void main(String[] args) throws Throwable {
					
		// Instantiate generic ProtocolBufferConverter with SimpleImage exemplar
		ProtocolBufferConverter<SimpleImage> converter = new ProtocolBufferConverter<SimpleImage>(SimpleImage.getDefaultInstance());
		
		// get a factory instance to create new RSB domain objects
		Factory factory = Factory.getInstance();

		// register converter for SimpleImage's
		DefaultConverterRepository.getDefaultConverterRepository().addConverter(converter);
		
		// create an informer on scope "/example/informer" to send event
		// notifications. This informer is capable of sending Strings.
		Informer<SimpleImage> informer = factory.createInformer(new Scope(
				"/example/informer"));

		// activate the informer to be ready for work
		informer.activate();

		// send several events using a method that accepts the data and
		// automatically creates an appropriate event internally.
		Builder img = SimpleImage.newBuilder();
		for (int i = 0; i < 100; i++) {			
			img.setHeight(100);
			img.setWidth(100);
			byte[] bytes = new byte[100*100];
			ByteString bs = ByteString.copyFrom(bytes);
			img.setData(bs);					
			informer.send(img.build());
			System.out.println("Sending image...");
		}

		// as there is no explicit removal model in java, always manually
		// deactivate the informer if it is not needed anymore
		informer.deactivate();

	}

}
