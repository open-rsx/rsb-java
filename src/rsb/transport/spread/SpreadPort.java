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
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.NoSuchConverterException;
import rsb.converter.WireContents;
import rsb.filter.AbstractFilterObserver;
import rsb.filter.FilterAction;
import rsb.filter.ScopeFilter;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.EventMetaData;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.transport.InConnector;
import rsb.transport.OutConnector;
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
public class SpreadPort extends AbstractFilterObserver implements InConnector,
        OutConnector {

    private final static Logger LOG = Logger.getLogger(SpreadPort.class
            .getName());

    private ReceiverTask receiver;
    private final EventHandler eventHandler;
    private static final int MIN_DATA_SIZE = 5;
    private static final int MAX_MSG_SIZE = 100000;

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

    private final SpreadWrapper spread;
    // TODO instantiate matching strategy, initially in the constructor, later
    // per configuration
    private final ConverterSelectionStrategy<ByteBuffer> inStrategy;
    private final ConverterSelectionStrategy<ByteBuffer> outStrategy;

    private interface QoSHandler {

        void apply(DataMessage message) throws SerializeException;
    }

    private class UnreliableHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setUnreliable();
        }

    }

    private class ReliableHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setReliable();
        }

    }

    private class FifoHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setFifo();
        }

    }

    /**
     * @param spread
     *            encapsulation of spread communication
     * @param eventHandler
     *            if <code>null</code>, no receiving of events will be done
     * @param inStrategy
     *            converters to use for receiving data
     * @param outStrategy
     *            converters to use for sending data
     */
    public SpreadPort(final SpreadWrapper spread,
            final EventHandler eventHandler,
            final ConverterSelectionStrategy<ByteBuffer> inStrategy,
            final ConverterSelectionStrategy<ByteBuffer> outStrategy) {
        this.spread = spread;
        this.eventHandler = eventHandler;
        this.inStrategy = inStrategy;
        this.outStrategy = outStrategy;

        // TODO initial hack to get QoS from properties, replace this with a
        // real participant config
        Ordering ordering = new QualityOfServiceSpec().getOrdering();
        try {
            ordering = Ordering.valueOf(Properties.getInstance().getProperty(
                    "qualityofservice.ordering"));
        } catch (final InvalidPropertyException e) {
            // valid case if property does not exist
        }
        Reliability reliability = new QualityOfServiceSpec().getReliability();
        try {
            reliability = Reliability.valueOf(Properties.getInstance()
                    .getProperty("qualityofservice.reliability"));
        } catch (final InvalidPropertyException e) {
            // valid case if property does not exist
        }
        this.setQualityOfServiceSpec(new QualityOfServiceSpec(ordering,
                reliability));
    }

    @Override
    public void activate() throws InitializeException {
        this.receiver = new ReceiverTask(this.spread, this.eventHandler,
                this.inStrategy);
        // activate spread connection
        if (!this.spread.isActive()) {
            this.spread.activate();
        }
        this.receiver.setPriority(Thread.NORM_PRIORITY + 2);
        this.receiver.setName("ReceiverTask [grp="
                + this.spread.getPrivateGroup() + "]");
        this.receiver.start();
    }

    @Override
    public void notify(final ScopeFilter e, final FilterAction a) {
        LOG.fine("SpreadPort::notify(ScopeFilter e, FilterAction=" + a.name()
                + " called");
        switch (a) {
        case ADD:
            // TODO add reference handling from xcf4j
            this.joinSpreadGroup(e.getScope());
            break;
        case REMOVE:
            // TODO add reference handling from xcf4j
            this.leaveSpreadGroup(e.getScope());
            break;
        case UPDATE:
            LOG.info("Update of ScopeFilter requested on SpreadSport");
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
    private String spreadGroupName(final Scope scope) {

        try {

            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.reset();
            digest.update(scope.toString().getBytes());
            final byte[] sum = digest.digest();
            assert sum.length == 16;

            final StringBuilder hexString = new StringBuilder();
            for (final byte element : sum) {
                String s = Integer.toHexString(0xFF & element);
                if (s.length() == 1) {
                    s = '0' + s;
                }
                hexString.append(s);
            }

            return hexString.toString().substring(0, 31);

        } catch (final NoSuchAlgorithmException e) {
            assert false : "There must be an md5 algorith available";
            throw new RuntimeException("Unable to find md5 algorithm", e);
        }

    }

    private EventId.Builder createEventIdBuilder(
            @SuppressWarnings("PMD.ShortVariable") final rsb.EventId id) {
        final EventId.Builder eventIdBuilder = EventId.newBuilder();
        eventIdBuilder.setSenderId(ByteString.copyFrom(id.getParticipantId()
                .toByteArray()));
        eventIdBuilder.setSequenceNumber((int) id.getSequenceNumber());
        return eventIdBuilder;
    }

    private void fillMandatoryNotificationFields(
            final Notification.Builder notificationBuilder, final Event event) {
        notificationBuilder
                .setEventId(this.createEventIdBuilder(event.getId()));
    }

    private void fillNotificationHeader(final Builder notificationBuilder,
            final Event event, final String wireSchema) {

        // notification metadata
        notificationBuilder.setWireSchema(ByteString.copyFromUtf8(wireSchema));
        notificationBuilder.setScope(ByteString.copyFromUtf8(event.getScope()
                .toString()));
        if (event.getMethod() != null) {
            notificationBuilder.setMethod(ByteString.copyFromUtf8(event
                    .getMethod()));
        }

        final EventMetaData.Builder metaDataBuilder = EventMetaData
                .newBuilder();
        metaDataBuilder.setCreateTime(event.getMetaData().getCreateTime());
        metaDataBuilder.setSendTime(event.getMetaData().getSendTime());
        for (final String key : event.getMetaData().userInfoKeys()) {
            final UserInfo.Builder infoBuilder = UserInfo.newBuilder();
            infoBuilder.setKey(ByteString.copyFromUtf8(key));
            infoBuilder.setValue(ByteString.copyFromUtf8(event.getMetaData()
                    .getUserInfo(key)));
            metaDataBuilder.addUserInfos(infoBuilder.build());
        }
        for (final String key : event.getMetaData().userTimeKeys()) {
            final UserTime.Builder timeBuilder = UserTime.newBuilder();
            timeBuilder.setKey(ByteString.copyFromUtf8(key));
            timeBuilder.setTimestamp(event.getMetaData().getUserTime(key));
            metaDataBuilder.addUserTimes(timeBuilder.build());
        }
        notificationBuilder.setMetaData(metaDataBuilder.build());
        for (final rsb.EventId cause : event.getCauses()) {
            notificationBuilder.addCauses(this.createEventIdBuilder(cause));
        }

    }

    private class Fragment {

        public FragmentedNotification.Builder fragmentBuilder = null;
        public Notification.Builder notificationBuilder = null;

        public Fragment(final FragmentedNotification.Builder fragmentBuilder,
                final Notification.Builder notificationBuilder) {
            this.fragmentBuilder = fragmentBuilder;
            this.notificationBuilder = notificationBuilder;
        }
    }

    @Override
    public void push(final Event event) throws ConversionException {

        WireContents<ByteBuffer> convertedDataBuffer;
        try {
            convertedDataBuffer = this.convertEvent(event);
        } catch (final NoSuchConverterException ex) {
            LOG.warning(ex.getMessage());
            return;
        }
        final int dataSize = convertedDataBuffer.getSerialization().limit();

        event.getMetaData().setSendTime(0);

        // find out how many messages are required to send the data
        // int requiredParts = 1;
        // if (dataSize > 0) {
        // requiredParts = (int) Math.ceil((float) dataSize
        // / (float) MAX_MSG_SIZE);
        // }

        // create a list of fragmented messages
        final List<Fragment> fragments = new ArrayList<Fragment>();
        int cursor = 0;
        int currentFragment = 0;
        // "currentFragment == 0" is required for the case when dataSize == 0
        while (cursor < dataSize || currentFragment == 0) {

            final FragmentedNotification.Builder fragmentBuilder = FragmentedNotification
                    .newBuilder();
            final Notification.Builder notificationBuilder = Notification
                    .newBuilder();

            this.fillMandatoryNotificationFields(notificationBuilder, event);

            // for the first notification we also need to set the whole head
            // with meta data etc.
            if (currentFragment == 0) {
                this.fillNotificationHeader(notificationBuilder, event,
                        convertedDataBuffer.getWireSchema());
            }

            // determine how much space can still be used for data
            // TODO this is really suboptimal with the java API...
            final FragmentedNotification.Builder fragmentBuilderClone = fragmentBuilder
                    .clone();
            fragmentBuilderClone.setNotification(notificationBuilder.clone());
            final int currentNotificationSize = fragmentBuilderClone
                    .buildPartial().getSerializedSize();
            if (currentNotificationSize > MAX_MSG_SIZE - MIN_DATA_SIZE) {
                throw new RuntimeException(
                        "There is not enough space for data in this message.");
            }
            final int maxDataPartSize = MAX_MSG_SIZE - currentNotificationSize;

            int fragmentDataSize = maxDataPartSize;
            if (cursor + fragmentDataSize > dataSize) {
                fragmentDataSize = dataSize - cursor;
            }
            final ByteString dataPart = ByteString.copyFrom(convertedDataBuffer
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
            for (final Fragment fragment : fragments) {
                fragment.fragmentBuilder.setNumDataParts(fragments.size());
            }
        }

        // send all fragments
        for (final Fragment fragment : fragments) {

            fragment.fragmentBuilder
                    .setNotification(fragment.notificationBuilder);

            // build final notification
            final FragmentedNotification serializedFragment = fragment.fragmentBuilder
                    .build();

            // send message on spread
            // TODO remove data message
            final DataMessage dm = new DataMessage();
            try {
                dm.setData(serializedFragment.toByteArray());
            } catch (final SerializeException ex) {
                throw new RuntimeException(
                        "Unable to set binary data for a spread message.", ex);
            }

            // send to all super scopes
            final List<Scope> scopes = event.getScope().superScopes(true);
            for (final Scope scope : scopes) {
                dm.addGroup(this.spreadGroupName(scope));
            }

            // apply QoS
            try {
                this.spreadServiceHandler.apply(dm);
            } catch (final SerializeException ex) {
                throw new RuntimeException(
                        "Unable to apply quality of service settings for a spread message.",
                        ex);
            }

            final boolean sent = this.spread.send(dm);
            assert sent;

        }

    }

    private WireContents<ByteBuffer> convertEvent(final Event event)
            throws ConversionException {
        Converter<ByteBuffer> converter = null;
        // convert data
        converter = this.outStrategy.getConverter(event.getType().getName());
        final WireContents<ByteBuffer> convertedDataBuffer = converter
                .serialize(event.getType(), event.getData());
        return convertedDataBuffer;
    }

    private void joinSpreadGroup(final Scope scope) {
        if (this.spread.isActive()) {
            // join group
            try {
                this.spread.join(this.spreadGroupName(scope));
            } catch (final SpreadException e) {
                throw new RuntimeException(
                        "Unable to join spread group for scope '" + scope
                                + "' with hash '" + this.spreadGroupName(scope)
                                + "'.", e);
            }
        } else {
            LOG.severe("Couldn't set up network filter, spread inactive.");
        }
    }

    private void leaveSpreadGroup(final Scope scope) {
        if (this.spread.isActive()) {
            this.spread.leave(this.spreadGroupName(scope));
        } else {
            LOG.severe("Couldn't remove group filter, spread inactive.");
        }
    }

    @Override
    public void deactivate() throws RSBException {
        if (this.spread.isActive()) {
            LOG.fine("deactivating SpreadPort");
            this.spread.deactivate();
        }
        try {
            this.receiver.join();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getType() {
        return "SpreadPort";
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec qos) {

        if (qos.getReliability() == QualityOfServiceSpec.Reliability.UNRELIABLE
                && qos.getOrdering() == QualityOfServiceSpec.Ordering.UNORDERED) {
            this.spreadServiceHandler = new UnreliableHandler();
        } else if (qos.getReliability() == QualityOfServiceSpec.Reliability.UNRELIABLE
                && qos.getOrdering() == QualityOfServiceSpec.Ordering.ORDERED) {
            this.spreadServiceHandler = new FifoHandler();
        } else if (qos.getReliability() == QualityOfServiceSpec.Reliability.RELIABLE
                && qos.getOrdering() == QualityOfServiceSpec.Ordering.UNORDERED) {
            this.spreadServiceHandler = new ReliableHandler();
        } else if (qos.getReliability() == QualityOfServiceSpec.Reliability.RELIABLE
                && qos.getOrdering() == QualityOfServiceSpec.Ordering.ORDERED) {
            this.spreadServiceHandler = new FifoHandler();
        } else {
            assert false;
        }

    }

    @Override
    public boolean isActive() {
        return this.spread.isActive();
    }
}
