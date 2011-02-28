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

import rsb.RSBEvent;
import rsb.event.EventId;
import rsb.protocol.NotificationPB.Notification;
import rsb.transport.AbstractConverter;
import rsb.transport.Router;
import rsb.util.Holder;
import spread.SpreadException;
import spread.SpreadMessage;

class ReceiverTask extends Thread {

	Logger log = Logger.getLogger(ReceiverTask.class.getName());

	/**
	 * SpreadConnection
	 */
	private SpreadWrapper spread;

	private SpreadMessageConverter smc = new SpreadMessageConverter();

	private Router r;

	private Map<String, AbstractConverter<ByteBuffer>> converters;
	
	/**
	 * @param spreadWrapper
	 * @param converters 
	 */
	ReceiverTask(SpreadWrapper spreadWrapper, Router r, Map<String, AbstractConverter<ByteBuffer>> converters) {
		this.spread = spreadWrapper;
		this.r = r;
		this.converters = converters;
	}

	public void run() {
		log.info("Listener thread started");
		while (spread.conn.isConnected()
				&& !Thread.currentThread().isInterrupted()) {
			try {
				SpreadMessage sm = spread.conn.receive();
				// TODO check whether membership messages shall be handled
				// similar to data messages and be converted into events
				// TODO evaluate return value
				DataMessage dm = smc.process(sm);
				if (dm!=null) {
					// TODO discuss whether we want to deserialize here or in the router
					// for now, we deserialize here
					log.fine("Notification reveived by ReceiverTask");
					RSBEvent e = convertNotification(dm);
					if (e!=null) {
						// dispatch event
						r.deliver(e);
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
					log.info("Spread connection is closed.");
					break;
				}
				if (!spread.shutdown) {
					log.warning("Caught a SpreadException while trying to receive a message: " + e1.getMessage());
				}
			}
		}
		log.info("Listener thread stopped");
	}

	// TODO think about wheter this could actually be a regular converter call 
	private RSBEvent convertNotification(DataMessage dm) {
		Notification n = null;
		try {
			n = Notification.parseFrom(dm.getData().array());
		} catch (InvalidProtocolBufferException e1) {			
			e1.printStackTrace();
			// TODO throw exception
		}
		if (n!=null) {
			log.fine("decoding notification");
			RSBEvent e = new RSBEvent(n.getTypeId());
			e.setUri(n.getUri());
			e.setId(new EventId(n.getEid()));
			// user data conversion
			// why not do this lazy after / in the filtering?
	        // TODO deal with missing converters, errors    	
	    	AbstractConverter<ByteBuffer> c = converters.get(e.getType());
	    	ByteBuffer bb = ByteBuffer.wrap(n.getData().getBinary().toByteArray());
			e.setData(c.deserialize(e.getType(), bb).value);
			log.finest("returning event with id: " + e.getId() );
			return e;
		}
		return null;		
		
	}

}