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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.RSBException;
import rsb.Scope;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.converter.Converter;
import rsb.converter.Converter.WireContents;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.protocol.Protocol.MetaData;
import rsb.protocol.Protocol.Notification;
import rsb.protocol.Protocol.UserInfo;
import rsb.protocol.Protocol.UserTime;
import rsb.transport.ConversionException;
import rsb.transport.AbstractPort;
import rsb.transport.EventHandler;
import rsb.util.InvalidPropertyException;
import rsb.util.Properties;
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

	private interface QoSHandler {
		void apply(DataMessage message) throws SerializeException;
	}

	private class UnreliableHandler implements QoSHandler {
		@Override
		public void apply(DataMessage message) throws SerializeException {
			message.getSpreadMessage().setUnreliable();
		}
	}

	private class ReliableHandler implements QoSHandler {
		@Override
		public void apply(DataMessage message) throws SerializeException {
			message.getSpreadMessage().setReliable();
		}
	}

	private class FifoHandler implements QoSHandler {
		@Override
		public void apply(DataMessage message) throws SerializeException {
			message.getSpreadMessage().setFifo();
		}
	}

	/**
	 * The message service type used for sending messages via spread.
	 */
	private QoSHandler spreadServiceHandler;

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

	private SpreadWrapper spread = null;
	private Map<String, Converter<ByteBuffer>> converters = new HashMap<String, Converter<ByteBuffer>>();

	/**
	 * @param sw
	 * @param eventHandler
	 *            if <code>null</code>, no receiving of events will be done
	 */
	public SpreadPort(SpreadWrapper sw, EventHandler eventHandler) {
		spread = sw;
		this.eventHandler = eventHandler;

		// TODO initial hack to get QoS from properties, replace this with a
		// real participant config
		Ordering ordering = new QualityOfServiceSpec().getOrdering();
		try {
			ordering = Ordering.valueOf(Properties.getInstance().getProperty(
					"qualityofservice.ordering"));
		} catch (InvalidPropertyException e) {
		}
		Reliability reliability = new QualityOfServiceSpec().getReliability();
		try {
			reliability = Reliability.valueOf(Properties.getInstance()
					.getProperty("qualityofservice.reliability"));
		} catch (InvalidPropertyException e) {
		}
		setQualityOfServiceSpec(new QualityOfServiceSpec(ordering, reliability));
	}

	public void activate() throws InitializeException {
		receiver = new ReceiverTask(spread, eventHandler, converters);
		// activate spread connection
		if (!spread.isActive()) {
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

	/**
	 * Creates the md5 hashed spread group names.
	 * 
	 * @param scope
	 *            scope to create group name
	 * @return truncated md5 hash to fit into spread group
	 */
	private String spreadGroupName(Scope scope) {

		try {

			MessageDigest digest = MessageDigest.getInstance("md5");
			digest.reset();
			digest.update(scope.toString().getBytes());
			byte[] sum = digest.digest();
			assert sum.length == 16;

			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < sum.length; i++) {
				String s = Integer.toHexString(0xFF & sum[i]);
				if (s.length() == 1) {
					s = '0' + s;
				}
				hexString.append(s);
			}

			return hexString.toString().substring(0, 31);

		} catch (NoSuchAlgorithmException e) {
			assert false : "There must be an md5 algorith available";
			throw new RuntimeException("Unable to find md5 algorithm", e);
		}

	}

	@Override
	public void push(Event e) throws ConversionException {

		// convert data
		// TODO deal with missing converter
		Converter<ByteBuffer> converter = converters.get(e.getType());
		WireContents<ByteBuffer> convertedDataBuffer = converter.serialize(
				e.getType(), e.getData());
		int dataSize = convertedDataBuffer.getSerialization().limit();

		// find out how many messages are required to send the data
		int requiredParts = 1;
		if (dataSize > 0) {
			requiredParts = (int) Math.ceil((float) dataSize
					/ (float) MAX_MSG_SIZE);
		}

		e.getMetaData().setSendTime(0);

		// send all parts
		for (int part = 0; part < requiredParts; ++part) {

			Notification.Builder notificationBuilder = Notification
					.newBuilder();

			// notification metadata
			notificationBuilder.setId(ByteString.copyFrom(e.getId()
					.toByteArray()));
			notificationBuilder.setWireSchema(ByteString
					.copyFromUtf8(convertedDataBuffer.getWireSchema()));
			notificationBuilder.setScope(ByteString.copyFromUtf8(e.getScope()
					.toString()));

			MetaData.Builder metaDataBuilder = MetaData.newBuilder();
			metaDataBuilder.setCreateTime(e.getMetaData().getCreateTime());
			metaDataBuilder.setSendTime(e.getMetaData().getSendTime());
			metaDataBuilder.setSenderId(ByteString.copyFrom(e.getMetaData()
					.getSenderId().toByteArray()));
			for (String key : e.getMetaData().userInfoKeys()) {
				UserInfo.Builder infoBuilder = UserInfo.newBuilder();
				infoBuilder.setKey(key);
				infoBuilder.setValue(e.getMetaData().getUserInfo(key));
				metaDataBuilder.addUserInfos(infoBuilder.build());
			}
			for (String key : e.getMetaData().userTimeKeys()) {
				UserTime.Builder timeBuilder = UserTime.newBuilder();
				timeBuilder.setKey(ByteString.copyFromUtf8(key));
				timeBuilder.setTimestamp(e.getMetaData().getUserTime(key));
				metaDataBuilder.addUserTimes(timeBuilder.build());
			}
			notificationBuilder.setMetaData(metaDataBuilder.build());

			// data fragmentation
			int fragmentSize = MAX_MSG_SIZE;
			if (part == requiredParts - 1) {
				fragmentSize = dataSize - MAX_MSG_SIZE * part;
			}
			ByteString dataPart = ByteString.copyFrom(convertedDataBuffer
					.getSerialization().array(), part * MAX_MSG_SIZE,
					fragmentSize);
			if (part != requiredParts - 1) {
				assert dataPart.size() == MAX_MSG_SIZE;
			}
			notificationBuilder.setData(dataPart);
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
			} catch (SerializeException ex) {
				throw new RuntimeException(
						"Unable to set binary data for a spread message.", ex);
			}

			// send to all super scopes
			List<Scope> scopes = e.getScope().superScopes(true);
			for (Scope scope : scopes) {
				dm.addGroup(spreadGroupName(scope));
			}

			// apply QoS
			try {
				spreadServiceHandler.apply(dm);
			} catch (SerializeException ex) {
				throw new RuntimeException(
						"Unable to apply quality of service settings for a spread message.",
						ex);
			}

			boolean sent = spread.send(dm);
			assert (sent);

		}

	}

	private void joinSpreadGroup(Scope scope) {
		if (spread.isActive()) {
			// join group
			try {
				spread.join(spreadGroupName(scope));
			} catch (SpreadException e) {
				throw new RuntimeException(
						"Unable to join spread group for scope '" + scope
								+ "' with hash '" + spreadGroupName(scope)
								+ "'.", e);
			}
		} else {
			log.severe("Couldn't set up network filter, spread inactive.");
		}
	}

	private void leaveSpreadGroup(Scope scope) {
		if (spread.isActive()) {
			spread.leave(spreadGroupName(scope));
		} else {
			log.severe("Couldn't remove group filter, spread inactive.");
		}
	}

	public void deactivate() throws RSBException {
		if (spread.isActive()) {
			log.fine("deactivating SpreadPort");
			spread.deactivate();
		}
		try {
			receiver.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getType() {
		return "SpreadPort";
	}

	public void addConverter(String typeInfo, Converter<ByteBuffer> converter) {
		converters.put(typeInfo, converter);
	}

	@Override
	public void setQualityOfServiceSpec(QualityOfServiceSpec qos) {

		if (qos.getReliability() == QualityOfServiceSpec.Reliability.UNRELIABLE
				&& qos.getOrdering() == QualityOfServiceSpec.Ordering.UNORDERED) {
			spreadServiceHandler = new UnreliableHandler();
		} else if (qos.getReliability() == QualityOfServiceSpec.Reliability.UNRELIABLE
				&& qos.getOrdering() == QualityOfServiceSpec.Ordering.ORDERED) {
			spreadServiceHandler = new FifoHandler();
		} else if (qos.getReliability() == QualityOfServiceSpec.Reliability.RELIABLE
				&& qos.getOrdering() == QualityOfServiceSpec.Ordering.UNORDERED) {
			spreadServiceHandler = new ReliableHandler();
		} else if (qos.getReliability() == QualityOfServiceSpec.Reliability.RELIABLE
				&& qos.getOrdering() == QualityOfServiceSpec.Ordering.ORDERED) {
			spreadServiceHandler = new FifoHandler();
		} else {
			assert false;
		}

	}

	@Override
	public boolean isActive() {
		return spread.isActive();
	}
}
