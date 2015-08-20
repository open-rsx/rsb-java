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
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.WireContents;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.ProtocolConversion;
import rsb.transport.OutConnector;
import rsb.util.ByteHelpers;

import com.google.protobuf.ByteString;

/**
 * An {@link OutConnector} for the spread daemon network.
 *
 * @author jwienke
 * @author swrede
 */
public class SpreadOutConnector implements OutConnector {

    private static final Logger LOG = Logger.getLogger(SpreadOutConnector.class
            .getName());

    private static final int MIN_DATA_SIZE = 5;

    /**
     * The maximum size of a spread message in bytes.
     */
    private static final int MAX_MSG_SIZE = 100000;

    /**
     * The message service type used for sending messages via spread.
     */
    private QoSHandler spreadServiceHandler;

    private final SpreadWrapper spread;
    private boolean active = false;

    /**
     * The converter selection strategy used to serialize messages.
     */
    private final ConverterSelectionStrategy<ByteBuffer> converters;

    /**
     * Implementing classes apply a specific spread message qos flag to a
     * message that will be sent later. They are used to apply the RSB qos
     * requirements.
     *
     * @author jwienke
     */
    private interface QoSHandler {

        /**
         * Applies the quality of service requirements by adapting the message
         * flags.
         *
         * @param message
         *            the message to modify
         * @throws SerializeException
         *             an exception unfortunately thrown by
         *             {@link DataMessage#getSpreadMessage()}. We should not
         *             need this ultimately
         */
        void apply(DataMessage message) throws SerializeException;

    }

    /**
     * Allows unreliable communication.
     *
     * @author jwienke
     */
    private class UnreliableHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setUnreliable();
        }

    }

    /**
     * Requires reliable communication.
     *
     * @author jwienke
     */
    private class ReliableHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setReliable();
        }

    }

    /**
     * Enforces FIFO ordering of sent messages.
     *
     * @author jwienke
     */
    private class FifoHandler implements QoSHandler {

        @Override
        public void apply(final DataMessage message) throws SerializeException {
            message.getSpreadMessage().setFifo();
        }

    }

    /**
     * Constructs a new {@link SpreadOutConnector}.
     *
     * @param spread
     *            encapsulation of spread communication. Must not be activated.
     * @param outStrategy
     *            converters to use for sending data
     * @param qos
     *            quality of service requirements to address
     */
    public SpreadOutConnector(final SpreadWrapper spread,
            final ConverterSelectionStrategy<ByteBuffer> outStrategy,
            final QualityOfServiceSpec qos) {

        this.spread = spread;
        this.converters = outStrategy;
        this.setQualityOfServiceSpec(qos);

    }

    @Override
    public void activate() throws RSBException {
        // activate spread connection
        if (this.active) {
            throw new IllegalStateException("Connector is already active.");
        }
        this.spread.activate();
        this.active = true;
    }

    /**
     * Represents a single fragment from a potentially larger message to be sent
     * via the spread connection.
     *
     * @author jwienke
     */
    private class Fragment {

        private final FragmentedNotification.Builder fragmentBuilder;
        private final Notification.Builder notificationBuilder;

        public Fragment(final FragmentedNotification.Builder fragmentBuilder,
                final Notification.Builder notificationBuilder) {
            this.fragmentBuilder = fragmentBuilder;
            this.notificationBuilder = notificationBuilder;
        }

        public FragmentedNotification.Builder getFragmentBuilder() {
            return this.fragmentBuilder;
        }

        public Notification.Builder getNotificationBuilder() {
            return this.notificationBuilder;
        }

    }

    @Override
    public void push(final Event event) throws RSBException {
        LOG.log(Level.FINEST, "Pushing event to the wire: {0}", event);

        final WireContents<ByteBuffer> convertedDataBuffer =
                ProtocolConversion.serializeEventData(event, this.converters);

        event.getMetaData().setSendTime(0);

        final List<Fragment> fragments =
                prepareFragments(event, convertedDataBuffer);
        LOG.log(Level.FINEST, "Event got fragmented into {0} parts",
                fragments.size());

        // send all fragments
        for (final Fragment fragment : fragments) {
            LOG.log(Level.FINEST, "Sending fragment {0}", fragment);

            fragment.getFragmentBuilder().setNotification(
                    fragment.getNotificationBuilder());

            // build final notification
            final FragmentedNotification serializedFragment =
                    fragment.getFragmentBuilder().build();

            // send message on spread
            // TODO remove data message
            final DataMessage message = new DataMessage();
            try {
                message.setData(serializedFragment.toByteArray());
            } catch (final SerializeException ex) {
                throw new ConversionException(
                        "Unable to set binary data for a spread message.", ex);
            }

            // send to all super scopes
            final List<Scope> scopes = event.getScope().superScopes(true);
            for (final Scope scope : scopes) {
                message.addGroup(SpreadUtilities.spreadGroupName(scope));
            }

            // apply QoS
            try {
                this.spreadServiceHandler.apply(message);
            } catch (final SerializeException ex) {
                throw new ConversionException(
                        "Unable to apply quality of service settings "
                                + "for a spread message.", ex);
            }

            try {
                this.spread.send(message);
            } catch (final SendException e) {
                LOG.log(Level.WARNING, "Unable to send message", e);
                throw new RSBException(e);
            }

        }

    }

    /**
     * Prepares the fragments which will be sent on the wire.
     *
     * @param event
     *            the event we are currently sending
     * @param convertedData
     *            the converted data to send on the wire
     * @return the created fragments
     * @throws ConversionException
     *             thrown in case there is so much meta data that no more user
     *             content fits into the fragments
     */
    private List<Fragment> prepareFragments(final Event event,
            final WireContents<ByteBuffer> convertedData)
            throws ConversionException {

        final int dataSize = convertedData.getSerialization().limit();

        final List<Fragment> fragments = new ArrayList<Fragment>();
        int cursor = 0;
        int currentFragment = 0;
        // "currentFragment == 0" is required for the case when dataSize == 0
        final byte[] data =
                ByteHelpers.byteBufferToArray(convertedData.getSerialization());

        while (cursor < dataSize || currentFragment == 0) {

            final FragmentedNotification.Builder fragmentBuilder =
                    FragmentedNotification.newBuilder();
            final Notification.Builder notificationBuilder =
                    Notification.newBuilder();

            notificationBuilder.setEventId(ProtocolConversion
                    .createEventIdBuilder(event.getId()));

            // for the first notification we also need to set the whole head
            // with meta data etc.
            if (currentFragment == 0) {
                ProtocolConversion.fillNotificationHeader(notificationBuilder,
                        event, convertedData.getWireSchema());
            }

            // determine how much space can still be used for data
            // TODO this is really suboptimal with the java API...
            final FragmentedNotification.Builder fragmentBuilderClone =
                    fragmentBuilder.clone();
            fragmentBuilderClone.setNotification(notificationBuilder.clone());
            final int thisNotificationSize =
                    fragmentBuilderClone.buildPartial().getSerializedSize();
            if (thisNotificationSize > MAX_MSG_SIZE - MIN_DATA_SIZE) {
                throw new ConversionException(
                        "There is not enough space for data in this message. "
                                + "Please reduce the meta data size.");
            }
            final int maxDataPartSize = MAX_MSG_SIZE - thisNotificationSize;

            int fragmentDataSize = maxDataPartSize;
            if (cursor + fragmentDataSize > dataSize) {
                fragmentDataSize = dataSize - cursor;
            }
            final ByteString dataPart =
                    ByteString.copyFrom(data, cursor, fragmentDataSize);

            notificationBuilder.setData(dataPart);
            fragmentBuilder.setDataPart(currentFragment);
            // optimistic guess
            fragmentBuilder.setNumDataParts(1);

            fragments.add(new Fragment(fragmentBuilder, notificationBuilder));

            cursor += fragmentDataSize;
            currentFragment++;

        }

        // update each fragment with the number of total fragments to expect
        // after we now know this number
        if (fragments.size() > 1) {
            for (final Fragment fragment : fragments) {
                fragment.getFragmentBuilder().setNumDataParts(fragments.size());
            }
        }

        return fragments;

    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        if (!this.active) {
            throw new IllegalStateException("Connector is not active.");
        }
        LOG.fine("deactivating SpreadPort");
        this.spread.deactivate();
        this.active = false;
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
        return this.active;
    }

    @Override
    public void setScope(final Scope scope) {
        // we don't care about the scope for sending. No need to declare it
    }

}
