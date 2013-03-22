/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010, 2011 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.transport.spread;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.Converter;
import rsb.converter.ConversionException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.NoSuchConverterException;
import rsb.converter.WireContents;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.EventMetaData;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.transport.AbstractPort;
import rsb.transport.EventHandler;
import rsb.util.InvalidPropertyException;
import rsb.util.Properties;
import spread.SpreadException;

import com.google.protobuf.ByteString;

/**
 * A port which connects to a spread daemon network.
 *
 * @author swrede
 */
public class SpreadPort extends AbstractPort {

	private ReceiverTask receiver;
	private EventHandler eventHandler;
	private static final int MIN_DATA_SIZE = 5;
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
	// TODO instantiate matching strategy, initially in the constructor, later
	// per configuration
	private ConverterSelectionStrategy<ByteBuffer> inStrategy;
	private ConverterSelectionStrategy<ByteBuffer> outStrategy;

	/**
	 * @param sw
	 * @param eventHandler
	 *            if <code>null</code>, no receiving of events will be done
	 * @param strategy
	 * @param outStrategy
	 */
	public SpreadPort(SpreadWrapper sw, EventHandler eventHandler,
			ConverterSelectionStrategy<ByteBuffer> inStrategy,
			ConverterSelectionStrategy<ByteBuffer> outStrategy) {
		spread = sw;
		this.eventHandler = eventHandler;
		this.inStrategy = inStrategy;
		this.outStrategy = outStrategy;

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
		receiver = new ReceiverTask(spread, eventHandler, inStrategy);
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
		log.fine("SpreadPort::notify(ScopeFilter e, FilterAction=" + a.name()
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

	private EventId.Builder createEventIdBuilder(final rsb.EventId id) {
		rsb.protocol.EventIdType.EventId.Builder eventIdBuilder = rsb.protocol.EventIdType.EventId
				.newBuilder();
		eventIdBuilder.setSenderId(ByteString.copyFrom(id.getParticipantId()
				.toByteArray()));
		eventIdBuilder.setSequenceNumber((int) id.getSequenceNumber());
		return eventIdBuilder;
	}

	private void fillMandatoryNotificationFields(
			Notification.Builder notificationBuilder, Event event) {
		notificationBuilder.setEventId(createEventIdBuilder(event.getId()));
	}

	private void fillNotificationHeader(Builder notificationBuilder,
			Event event, String wireSchema) {

		// notification metadata
		notificationBuilder.setWireSchema(ByteString.copyFromUtf8(wireSchema));
		notificationBuilder.setScope(ByteString.copyFromUtf8(event.getScope()
				.toString()));
		if (event.getMethod() != null) {
			notificationBuilder.setMethod(ByteString.copyFromUtf8(event
					.getMethod()));
		}

		EventMetaData.Builder metaDataBuilder = EventMetaData.newBuilder();
		metaDataBuilder.setCreateTime(event.getMetaData().getCreateTime());
		metaDataBuilder.setSendTime(event.getMetaData().getSendTime());
		for (String key : event.getMetaData().userInfoKeys()) {
			UserInfo.Builder infoBuilder = UserInfo.newBuilder();
			infoBuilder.setKey(ByteString.copyFromUtf8(key));
			infoBuilder.setValue(ByteString.copyFromUtf8(event.getMetaData()
					.getUserInfo(key)));
			metaDataBuilder.addUserInfos(infoBuilder.build());
		}
		for (String key : event.getMetaData().userTimeKeys()) {
			UserTime.Builder timeBuilder = UserTime.newBuilder();
			timeBuilder.setKey(ByteString.copyFromUtf8(key));
			timeBuilder.setTimestamp(event.getMetaData().getUserTime(key));
			metaDataBuilder.addUserTimes(timeBuilder.build());
		}
		notificationBuilder.setMetaData(metaDataBuilder.build());
		for (rsb.EventId cause : event.getCauses()) {
			notificationBuilder.addCauses(createEventIdBuilder(cause));
		}

	}

	private class Fragment {
		public FragmentedNotification.Builder fragmentBuilder = null;
		public Notification.Builder notificationBuilder = null;

		public Fragment(FragmentedNotification.Builder fragmentBuilder,
				Notification.Builder notificationBuilder) {
			this.fragmentBuilder = fragmentBuilder;
			this.notificationBuilder = notificationBuilder;
		}
	}

	@Override
	public void push(Event event) throws ConversionException {

		WireContents<ByteBuffer> convertedDataBuffer;
		try {
			convertedDataBuffer = convertEvent(event);
		} catch (NoSuchConverterException ex) {
			log.warning(ex.getMessage());
			return;
		}
		int dataSize = convertedDataBuffer.getSerialization().limit();

		event.getMetaData().setSendTime(0);

		// find out how many messages are required to send the data
		// int requiredParts = 1;
		// if (dataSize > 0) {
		// requiredParts = (int) Math.ceil((float) dataSize
		// / (float) MAX_MSG_SIZE);
		// }

		// create a list of fragmented messages
		List<Fragment> fragments = new ArrayList<Fragment>();
		int cursor = 0;
		int currentFragment = 0;
		// "currentFragment == 0" is required for the case when dataSize == 0
		while (cursor < dataSize || currentFragment == 0) {

			FragmentedNotification.Builder fragmentBuilder = FragmentedNotification
					.newBuilder();
			Notification.Builder notificationBuilder = Notification
					.newBuilder();

			fillMandatoryNotificationFields(notificationBuilder, event);

			// for the first notification we also need to set the whole head
			// with meta data etc.
			if (currentFragment == 0) {
				fillNotificationHeader(notificationBuilder, event,
						convertedDataBuffer.getWireSchema());
			}

			// determine how much space can still be used for data
			// TODO this is really suboptimal with the java API...
			FragmentedNotification.Builder fragmentBuilderClone = fragmentBuilder
					.clone();
			fragmentBuilderClone.setNotification(notificationBuilder.clone());
			int currentNotificationSize = fragmentBuilderClone.buildPartial()
					.getSerializedSize();
			if (currentNotificationSize > MAX_MSG_SIZE - MIN_DATA_SIZE) {
				throw new RuntimeException(
						"There is not enough space for data in this message.");
			}
			int maxDataPartSize = MAX_MSG_SIZE - currentNotificationSize;

			int fragmentDataSize = maxDataPartSize;
			if (cursor + fragmentDataSize > dataSize) {
				fragmentDataSize = dataSize - cursor;
			}
			ByteString dataPart = ByteString.copyFrom(convertedDataBuffer
					.getSerialization().array(), cursor, fragmentDataSize);

			notificationBuilder.setData(dataPart);
			fragmentBuilder.setDataPart(currentFragment);
			// optimistic guess
			fragmentBuilder.setNumDataParts(1);

			fragments.add(new Fragment(fragmentBuilder, notificationBuilder));

			cursor += fragmentDataSize;
			currentFragment++;

		}

		// check how many fragments we really need to send
		if (fragments.size() > 1) {
			for (Fragment fragment : fragments) {
				fragment.fragmentBuilder.setNumDataParts(fragments.size());
			}
		}

		// send all fragments
		for (Fragment fragment : fragments) {

			fragment.fragmentBuilder
					.setNotification(fragment.notificationBuilder);

			// build final notification
			FragmentedNotification serializedFragment = fragment.fragmentBuilder
					.build();

			// send message on spread
			// TODO remove data message
			DataMessage dm = new DataMessage();
			try {
				dm.setData(serializedFragment.toByteArray());
			} catch (SerializeException ex) {
				throw new RuntimeException(
						"Unable to set binary data for a spread message.", ex);
			}

			// send to all super scopes
			List<Scope> scopes = event.getScope().superScopes(true);
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

	private WireContents<ByteBuffer> convertEvent(Event event)
			throws ConversionException {
		Converter<ByteBuffer> converter = null;
		// convert data
		// TODO check if this is the correct assumption, not sure
		//      for instance if the type is still correctly 
		//      transmitted
		if ((event.getType() == null) || (event.getData() == null)) {
			// special handling for void type
			converter = outStrategy.getConverter("null");
		} else {
			converter = outStrategy.getConverter(event.getType().getName());
		}
		WireContents<ByteBuffer> convertedDataBuffer = converter.serialize(
				event.getType(), event.getData());
		return convertedDataBuffer;
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
