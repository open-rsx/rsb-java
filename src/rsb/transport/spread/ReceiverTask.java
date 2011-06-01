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
import java.util.Map;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import rsb.Event;
import rsb.EventId;
import rsb.Scope;
import rsb.protocol.Protocol.Notification;
import rsb.transport.AbstractConverter;
import rsb.transport.EventHandler;
import spread.SpreadException;
import spread.SpreadMessage;

class ReceiverTask extends Thread {

	Logger log = Logger.getLogger(ReceiverTask.class.getName());

	/**
	 * SpreadConnection
	 */
	private SpreadWrapper spread;

	private SpreadMessageConverter smc = new SpreadMessageConverter();

	private EventHandler eventHandler;

	private Map<String, AbstractConverter<ByteBuffer>> converters;

	private AssemblyPool pool = new AssemblyPool();

	/**
	 * @param spreadWrapper
	 * @param converters
	 */
	ReceiverTask(SpreadWrapper spreadWrapper, EventHandler r,
			Map<String, AbstractConverter<ByteBuffer>> converters) {
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
				Event e = new Event(n.getWireSchema().toStringUtf8());
				e.setScope(new Scope(n.getScope().toStringUtf8()));
				e.setId(new EventId(n.getId().toByteArray()));
				// user data conversion
				// why not do this lazy after / in the filtering?
				// TODO deal with missing converters, errors
				AbstractConverter<ByteBuffer> c = converters.get(e.getType());
				e.setData(c.deserialize(e.getType(), joinedData).value);
				log.finest("returning event with id: " + e.getId());

				return e;

			} else {
				return null;
			}

		} catch (InvalidProtocolBufferException e1) {
			e1.printStackTrace();
			// TODO throw exception
			return null;
		}

	}
}