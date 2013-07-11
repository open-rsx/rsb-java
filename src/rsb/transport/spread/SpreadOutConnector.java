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
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.EventMetaData;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.transport.OutConnector;
import rsb.util.InvalidPropertyException;
import rsb.util.Properties;

import com.google.protobuf.ByteString;

/**
 * An {@link OutConnector} for the spread daemon network.
 *
 * @author jwienke
 * @author swrede
 */
public class SpreadOutConnector implements OutConnector {

    private final static Logger LOG = Logger.getLogger(SpreadOutConnector.class
            .getName());

    private static final int MIN_DATA_SIZE = 5;
    private static final int MAX_MSG_SIZE = 100000;

    /**
     * The message service type used for sending messages via spread.
     */
    private QoSHandler spreadServiceHandler;

    private final SpreadWrapper spread;
    // TODO instantiate matching strategy, initially in the constructor, later
    // per configuration
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
     * @param outStrategy
     *            converters to use for sending data
     */
    public SpreadOutConnector(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> outStrategy) {
        this.spread = spread;
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
        // activate spread connection
        if (!this.spread.isActive()) {
            this.spread.activate();
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
                dm.addGroup(SpreadUtilities.spreadGroupName(scope));
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

    @Override
    public void deactivate() throws RSBException {
        if (this.spread.isActive()) {
            LOG.fine("deactivating SpreadPort");
            this.spread.deactivate();
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
