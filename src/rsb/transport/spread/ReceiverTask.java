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

    private final SpreadMessageConverter smc = new SpreadMessageConverter();

    private final EventHandler eventHandler;

    private final ConverterSelectionStrategy<ByteBuffer> converters;

    private final AssemblyPool pool = new AssemblyPool();

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
        this.spread = spreadWrapper;
        this.eventHandler = handler;
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
                LOG.fine("Message received from spread, message type: "
                        + receivedMessage.isRegular());

                // TODO check whether membership messages shall be handled
                // similar to data messages and be converted into events
                // TODO evaluate return value
                final DataMessage receivedData =
                        this.smc.process(receivedMessage);
                if (receivedData == null) {
                    continue;
                }

                LOG.fine("Notification reveived by ReceiverTask");
                final Event receivedFullEvent =
                        this.convertNotification(receivedData);
                if (receivedFullEvent != null) {
                    // dispatch event
                    this.eventHandler.handle(receivedFullEvent);
                }

            } catch (final InterruptedIOException e1) {
                LOG.info("Listener thread was interrupted during IO.");
                break;
            } catch (final SpreadException e1) {
                if (!this.spread.isConnected()) {
                    LOG.fine("Spread connection is closed.");
                }
                if (!this.spread.isShutdown()) {
                    LOG.log(Level.WARNING, "Caught a SpreadException while "
                            + "trying to receive a message", e1);
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

    // TODO think about whether this could actually be a regular converter call
    /**
     * Method for converting Spread data messages into Java RSB events. The main
     * purpose of this method is the conversion of Spread data messages by
     * parsing the notification data structures as defined in RSB.Protocol using
     * the ProtoBuf data holder classes.
     *
     * @param receivedData
     *            data gathered from the wire
     * @return deserialized event or null if the event was fragmented and the
     *         given data does not complete an event
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

            // TODO better error handling with callback object
        } catch (final InvalidProtocolBufferException e1) {
            LOG.log(Level.SEVERE, "Error decoding protocol buffer", e1);
            return null;
        } catch (final ConversionException e1) {
            LOG.log(Level.SEVERE, "Error deserializing user data", e1);
            return null;
        }

    }
}
