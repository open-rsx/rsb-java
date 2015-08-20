/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.converter.ConversionException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.protocol.FragmentedNotificationType.FragmentedNotification;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.ProtocolConversion;
import rsb.transport.EventHandler;
import rsb.util.ByteHelpers;
import spread.MembershipInfo;
import spread.SpreadException;
import spread.SpreadMessage;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * A task that continuously reads on a spread connection and decodes RSB
 * notifications from it.
 *
 * @author jwienke
 */
class ReceiverTask extends Thread {

    private static final Logger LOG = Logger.getLogger(ReceiverTask.class
            .getName());

    private final SpreadWrapper spread;

    private final EventHandler eventHandler;

    private MembershipHandler membershipHandler = null;

    private final ConverterSelectionStrategy<ByteBuffer> converters;

    private final AssemblyPool pool = new AssemblyPool();

    /**
     * Interface for classes that handle membership changes.
     *
     * @author jwienke
     */
    public interface MembershipHandler {

        /**
         * Called in case a new member has joined a group.
         *
         * @param group
         *            the group that was joined
         * @param memberGroup
         *            the private group of the member that joined.
         * @return if <code>true</code>, deregister this handler and do not call
         *         it further.
         */
        boolean joined(String group, String memberGroup);

    }

    /**
     * @param spreadWrapper
     *            the spread wrapper to receive from
     * @param handler
     *            handler for received events
     * @param converters
     *            converter set to use for deserialization
     */
    ReceiverTask(final SpreadWrapper spreadWrapper, final EventHandler handler,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        this(spreadWrapper, handler, null, converters);
    }

    /**
     * @param spreadWrapper
     *            the spread wrapper to receive from
     * @param eventHandler
     *            handler for received events
     * @param membershipHandler
     *            handler that processes membership changes
     * @param converters
     *            converter set to use for deserialization
     */
    ReceiverTask(final SpreadWrapper spreadWrapper,
            final EventHandler eventHandler,
            final MembershipHandler membershipHandler,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        this.spread = spreadWrapper;
        this.eventHandler = eventHandler;
        this.membershipHandler = membershipHandler;
        this.converters = converters;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void run() {
        LOG.finer("Listener thread started");
        while (this.spread.isConnected()
                && !Thread.currentThread().isInterrupted()) {
            try {
                final SpreadMessage receivedMessage = this.spread.receive();
                LOG.log(Level.FINEST,
                        "Message received from spread, message type: {0}",
                        receivedMessage.getType());

                if (receivedMessage.isRegular()) {
                    // handle regular messages first because this should be the
                    // more common case. This prevents a comparison operation
                    LOG.finest("Received message is a regular message");
                    handleDataMessage(receivedMessage);
                } else if (receivedMessage.isMembership()) {
                    LOG.finest("Received message is a membership message");
                    handleMembershipMessage(receivedMessage);
                } else {
                    LOG.log(Level.WARNING,
                            "Received a message with is neither a data "
                                    + "nor a membership message: {0}",
                            new Object[] { receivedMessage });
                }

            } catch (final InterruptedIOException e) {
                LOG.info("Listener thread was interrupted during IO.");
                break;
            } catch (final SpreadException e) {
                if (!this.spread.isConnected()) {
                    LOG.fine("Spread connection is closed.");
                }
                if (!this.spread.isShutdown()) {
                    LOG.log(Level.WARNING, "Caught a SpreadException while "
                            + "trying to receive a message", e);
                }
                // get out here, stop this thread as no further messages can be
                // retrieved
                // re-initialization elsewhere is necessary
                // TODO call error handler to allow framework shutdown from
                // client-code
                this.interrupt();
            }
        }
        LOG.fine("Listener thread stopped");
    }

    // allow catching generic exceptions to prevent being crashed by errors as
    // long as no active error handling is available
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void handleDataMessage(final SpreadMessage receivedMessage) {
        assert receivedMessage.isRegular();

        try {
            final DataMessage receivedData =
                    DataMessage.convertSpreadMessage(receivedMessage);

            Event receivedFullEvent = null;
            try {
                receivedFullEvent = this.convertNotification(receivedData);
                LOG.log(Level.FINEST, "receivedFullEvent={0}",
                        receivedFullEvent);
            } catch (final RuntimeException e) {
                // catch anything else so that we cannot be actively crashed
                // with bad network input
                LOG.log(Level.SEVERE,
                        "Error decoding event from the network layer", e);
                return;
            }
            // receivedFullEvent might be null in case a fragment was
            // received and no complete event is available yet
            if (receivedFullEvent == null) {
                return;
            }

            LOG.log(Level.FINEST, "Dispatching received event to handler {0}",
                    this.eventHandler);
            this.eventHandler.handle(receivedFullEvent);

        } catch (final SerializeException e) {
            LOG.log(Level.WARNING, "Error de-serializing SpreadMessage", e);
        }

    }

    private void handleMembershipMessage(final SpreadMessage receivedMessage) {
        if (this.membershipHandler == null) {
            return;
        }
        assert receivedMessage.isMembership();

        final MembershipInfo membershipInfo =
                receivedMessage.getMembershipInfo();
        boolean deregister = false;
        if (membershipInfo.isCausedByJoin()) {
            LOG.log(Level.FINEST, "Notifying MembershipHandler {0} "
                    + "about member {1} joining group {2}", new Object[] {
                    this.membershipHandler, membershipInfo.getJoined(),
                    membershipInfo.getGroup() });
            deregister =
                    this.membershipHandler.joined(membershipInfo.getGroup()
                            .toString(), membershipInfo.getJoined().toString());
        }
        if (deregister) {
            this.membershipHandler = null;
        }

    }

    /**
     * Method for converting Spread data messages into Java RSB events. The main
     * purpose of this method is the conversion of Spread data messages by
     * parsing the notification data structures as defined in RSB.Protocol using
     * the ProtoBuf data holder classes.
     *
     * @param receivedData
     *            data gathered from the wire
     * @return deserialized event or <code>null</code> if the event was
     *         fragmented and the given data does not complete an event
     */
    private Event convertNotification(final DataMessage receivedData) {

        try {

            final FragmentedNotification fragment =
                    FragmentedNotification.parseFrom(ByteHelpers
                            .byteBufferToArray(receivedData.getData()));
            final AssemblyPool.DataAndNotification joinedData =
                    this.pool.insert(fragment);

            if (joinedData == null) {
                return null;
            }

            final Notification initialNotification =
                    joinedData.getNotification();
            return ProtocolConversion.fromNotification(initialNotification,
                    joinedData.getData(), this.converters);

        } catch (final InvalidProtocolBufferException e) {
            LOG.log(Level.SEVERE, "Error decoding protocol buffer", e);
            return null;
        } catch (final ConversionException e) {
            LOG.log(Level.SEVERE, "Error deserializing user data", e);
            return null;
        }

    }
}
