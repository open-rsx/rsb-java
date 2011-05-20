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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.Event;
import rsb.RSBException;
import rsb.Scope;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.protocol.AttachmentPB.Attachment;
import rsb.protocol.NotificationPB.Notification;
import rsb.transport.AbstractConverter;
import rsb.transport.AbstractPort;
import rsb.transport.EventHandler;
import rsb.transport.convert.ByteBufferConverter;
import rsb.util.Holder;
import spread.SpreadException;

import com.google.protobuf.ByteString;

/**
 * @author swrede
 */
public class SpreadPort extends AbstractPort {

	private ReceiverTask receiver;
	private EventHandler eventHandler;
	private static final int MAX_MSG_SIZE = 100000;

	private final static Logger log = Logger.getLogger(SpreadPort.class
			.getName());

	/**
	 * Protocol for optimization based on registered filters: TypeFilter: Some
	 * types may be received via special spread groups, e.g. SystemEvents. Port
	 * could join groups for registered types only and send events of this type
	 * to the same groups IdentityFilter: This depends on whether the component
	 * is filtering for it's own identity, to receive private messages (could
	 * use spread's private groups here, but that would prevent us from
	 * intercepting this communication), or filtering for another components
	 * identity, e.g. a publisher. ScopeFilter: Restricts visibility according
	 * to the group encoding rules based on the Scope concept. XPathFilter: no
	 * way to optimize this on the Port
	 */

	SpreadWrapper spread = null;
	Map<String, AbstractConverter<ByteBuffer>> converters = new HashMap<String, AbstractConverter<ByteBuffer>>();

	/**
	 * @param sw
	 * @param eventHandler
	 *            if <code>null</code>, no receiving of events will be done
	 */
	public SpreadPort(SpreadWrapper sw, EventHandler eventHandler) {
		spread = sw;
		this.eventHandler = eventHandler;
	}

	public void activate() throws InitializeException {
		receiver = new ReceiverTask(spread, eventHandler, converters);
		// activate spread connection
		if (!spread.isActivated()) {
			spread.activate();
		}
		receiver.setPriority(Thread.NORM_PRIORITY + 2);
		receiver.setName("ReceiverTask [grp=" + spread.getPrivateGroup() + "]");
		receiver.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rsb.filter.AbstractFilterObserver#notify(rsb.filter.ScopeFilter,
	 * rsb.filter.FilterAction)
	 */
	@Override
	public void notify(ScopeFilter e, FilterAction a) {
		log.info("SpreadPort::notify(ScopeFilter e, FilterAction=" + a.name()
				+ " called");
		switch (a) {
		case ADD:
			// TODO add reference handling from xcf4j
			joinSpreadGroup(e.getScope());
			break;
		case REMOVE:
			// TODO add reference handling from xcf4j
			leaveSpreadGroup(e.getScope());
			break;
		case UPDATE:
			log.info("Update of ScopeFilter requested on SpreadSport");
			break;
		default:
			break;
		}
	}

	public void push(Event e) {

		// convert data
		// TODO deal with missing converter
		AbstractConverter<ByteBuffer> converter = converters.get(e.getType());
		Holder<ByteBuffer> convertedDataBuffer = converter.serialize("string",
				e.getData());
		int dataSize = convertedDataBuffer.value.limit();

		// find out how many messages are required to send the data
		int requiredParts = 1;
		if (dataSize > 0) {
			requiredParts = (int) Math.ceil((float) dataSize
					/ (float) MAX_MSG_SIZE);
		}

		// send all parts
		for (int part = 0; part < requiredParts; ++part) {

			Notification.Builder notificationBuilder = Notification
					.newBuilder();

			// notification metadata
			notificationBuilder.setId(e.getId().toString());
			notificationBuilder.setWireSchema(e.getType());
			notificationBuilder.setScope(e.getScope().toString());

			// data fragmentation
			int fragmentSize = MAX_MSG_SIZE;
			if (part == requiredParts - 1) {
				fragmentSize = dataSize % MAX_MSG_SIZE;
			}
			ByteString dataPart = ByteString.copyFrom(
					convertedDataBuffer.value.array(), part * MAX_MSG_SIZE,
					fragmentSize);
			if (part != requiredParts - 1) {
				assert dataPart.size() == MAX_MSG_SIZE;
			}
			Attachment.Builder attachmentBuilder = Attachment.newBuilder();
			attachmentBuilder.setBinary(dataPart);
			attachmentBuilder.setLength(dataPart.size());
			notificationBuilder.setData(attachmentBuilder.build());
			notificationBuilder.setDataPart(part);
			notificationBuilder.setNumDataParts(requiredParts);

			// build final notification
			Notification notification = notificationBuilder.build();
			log.fine("push called, sending message fragment " + (part + 1)
					+ "/" + requiredParts + " on port infrastructure: [eid="
					+ e.getId().toString() + "]");

			// send message on spread
			// TODO remove data message
			DataMessage dm = new DataMessage();
			try {
				dm.setData(notification.toByteArray());
			} catch (SerializeException e1) {
				// TODO think about reasonable error handling
				e1.printStackTrace();
			}
			dm.addGroup(e.getScope().toString());

			boolean sent = spread.send(dm);
			assert (sent);

		}

	}

	private void joinSpreadGroup(Scope scope) {
		if (spread.isActivated()) {
			// join group
			try {
				spread.join(scope.toString());
			} catch (SpreadException e) {
				// TODO how to handle this exception
				e.printStackTrace();
			}
		} else {
			log.severe("Couldn't set up network filter, spread inactive.");
		}
	}

	private void leaveSpreadGroup(Scope scope) {
		if (spread.isActivated()) {
			spread.leave(scope.toString());
		} else {
			log.severe("Couldn't remove group filter, spread inactive.");
		}
	}

	public void deactivate() throws RSBException {
		if (spread.isActivated()) {
			log.fine("deactivating SpreadPort");
			spread.deactivate();
		}
		try {
			receiver.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getType() {
		return "SpreadPort";
	}

	public void addConverter(String s, ByteBufferConverter bbc) {
		converters.put(s, bbc);
	}
}
