/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.junit.Test;

import rsb.ParticipantId;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.EventIdType.EventId;

import com.google.protobuf.ByteString;

/**
 * @author swrede
 */
public class ProtocolBufferConverterTest {

	final static Logger LOG = Logger
			.getLogger(ProtocolBufferConverterTest.class.getName());

	WireContents<ByteBuffer> buffer;
	ProtocolBufferConverter<Notification> converter = new ProtocolBufferConverter<Notification>(
			Notification.getDefaultInstance());

	/**
	 * Test method for
	 * {@link rsb.converter.ProtocolBufferConverter#serialize(java.lang.String, java.lang.Object)}
	 * .
	 * 
	 * @throws ConversionException
	 */
	public void testSerialize() throws ConversionException {
		Notification.Builder notificationBuilder = Notification.newBuilder();
		// notification metadata
		EventId.Builder eventIdBuilder = EventId.newBuilder();
		eventIdBuilder.setSequenceNumber(23);
		eventIdBuilder.setSenderId(ByteString.copyFrom(new ParticipantId()
				.toByteArray()));
		notificationBuilder.setEventId(eventIdBuilder);
		notificationBuilder.setWireSchema(ByteString
				.copyFromUtf8("rsb.notification"));
		notificationBuilder.setScope(ByteString.copyFromUtf8("rsb"));

		buffer = converter.serialize(Notification.class,
				notificationBuilder.build());
		assertNotNull(buffer);
	}

	/**
	 * Test method for
	 * {@link rsb.converter.ProtocolBufferConverter#deserialize(java.lang.String, java.nio.ByteBuffer)}
	 * .
	 * 
	 * @throws ConversionException
	 */
	public void testDeserialize() throws ConversionException {
		assertNotNull(buffer);
		UserData<Notification> result = converter.deserialize(
				".rsb.protocol.Notification", buffer.getSerialization());
		Notification n = (Notification) result.getData();
		assertEquals(23, n.getEventId().getSequenceNumber());
	}

	@Test
	public void testRoundtrip() throws ConversionException {
		testSerialize();
		testDeserialize();
	}

}
