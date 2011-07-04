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
package rsb.transport.spread;

import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import rsb.Event;
import rsb.Id;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.Converter.UserData;
import rsb.converter.ConverterSelectionStrategy;
import rsb.protocol.Protocol.Notification;
import rsb.protocol.Protocol.UserInfo;
import rsb.protocol.Protocol.UserTime;
import rsb.transport.EventHandler;
import spread.SpreadException;
import spread.SpreadMessage;

/**
 * A task that continuously reads on a spread connection and decodes RSB
 * notifications from it.
 * 
 * @author jwienke
 */
class ReceiverTask extends Thread {

	private Logger log = Logger.getLogger(ReceiverTask.class.getName());

	/**
	 * SpreadConnection
	 */
	private SpreadWrapper spread;

	private SpreadMessageConverter smc = new SpreadMessageConverter();

	private EventHandler eventHandler;

	private ConverterSelectionStrategy<ByteBuffer> converters;

	private AssemblyPool pool = new AssemblyPool();

	/**
	 * @param spreadWrapper
	 * @param converters
	 */
	ReceiverTask(SpreadWrapper spreadWrapper, EventHandler r,
			ConverterSelectionStrategy<ByteBuffer> converters) {
		this.spread = spreadWrapper;
		this.eventHandler = r;
		this.converters = converters;
	}

	public void run() {
		log.finer("Listener thread started");
		while (spread.conn.isConnected()
				&& !Thread.currentThread().isInterrupted()) {
			try {
				SpreadMessage sm = spread.conn.receive();
				log.fine("Message received from spread, message type: "
						+ sm.isRegular() + ", data = "
						+ new String(sm.getData()));
				// TODO check whether membership messages shall be handled
				// similar to data messages and be converted into events
				// TODO evaluate return value
				DataMessage dm = smc.process(sm);
				if (dm != null) {
					log.fine("Notification reveived by ReceiverTask");
					Event e = convertNotification(dm);
					if (e != null) {
						// dispatch event
						eventHandler.handle(e);
					}
				}
			} catch (InterruptedIOException e1) {
				log.info("Listener thread was interrupted during IO.");
				break;
			} catch (SpreadException e1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				if (!spread.conn.isConnected()) {
					log.fine("Spread connection is closed.");
					break;
				}
				if (!spread.shutdown) {
					log.warning("Caught a SpreadException while trying to receive a message: "
							+ e1.getMessage());
				}
			}
		}
		log.fine("Listener thread stopped");
	}

	// TODO think about whether this could actually be a regular converter call
	private Event convertNotification(DataMessage dm) {

		try {

			Notification n = Notification.parseFrom(dm.getData().array());
			ByteBuffer joinedData = pool.insert(n);

			if (joinedData != null) {

				log.fine("decoding notification");
				Event e = new Event();
				e.setScope(new Scope(n.getScope().toStringUtf8()));
				e.setId(new Id(n.getId().toByteArray()));
				// user data conversion
				// why not do this lazy after / in the filtering?
				// TODO deal with missing converters, errors
				Converter<ByteBuffer> c = converters.getConverter(n.getWireSchema().toStringUtf8());
				UserData userData = c.deserialize(n.getWireSchema()
						.toStringUtf8(), joinedData);
				e.setData(userData.getData());
				e.setType(userData.getTypeInfo());
				log.finest("returning event with id: " + e.getId());

				// metadata
				e.getMetaData().setSenderId(
						new Id(n.getMetaData().getSenderId().toByteArray()));
				e.getMetaData().setCreateTime(n.getMetaData().getCreateTime());
				e.getMetaData().setSendTime(n.getMetaData().getSendTime());
				e.getMetaData().setReceiveTime(0);
				for (UserInfo info : n.getMetaData().getUserInfosList()) {
					e.getMetaData().setUserInfo(info.getKey(), info.getValue());
				}
				for (UserTime time : n.getMetaData().getUserTimesList()) {
					e.getMetaData().setUserTime(time.getKey().toStringUtf8(),
							time.getTimestamp());
				}

				return e;

			} else {
				return null;
			}

			// TODO better error handling with callback object
		} catch (InvalidProtocolBufferException e1) {
			log.log(Level.SEVERE, "Error decoding protocol buffer", e1);
			return null;
		} catch (ConversionException e1) {
			log.log(Level.SEVERE, "Error deserializing user data", e1);
			return null;
		}

	}
}