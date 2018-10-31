/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.transport.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import rsb.Event;
import rsb.EventId;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.LongConverter;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.protocol.ProtocolConversion;
import rsb.util.ByteHelpers;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

//CHECKSTYLE.OFF: MagicNumber - test values
//CHECKSTYLE.OFF: JavadocMethod - test class

/**
 * Utilities for testing the socket-based transport.
 *
 * @author jwienke
 */
public final class Utilities {

    private Utilities() {
        super();
        // prevent initialization of utility class
    }

    public static int getSocketPort() {
        final ConfigLoader loader = new ConfigLoader();
        return loader.load(new Properties())
                .getProperty("transport.socket.port", "55443").asInteger();
    }

    public static InetAddress getSocketHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public static SocketOptions getSocketOptions() throws UnknownHostException {
        return new SocketOptions(getSocketHost(), getSocketPort(), true);
    }

    public static Notification createNotification() throws ConversionException {

        // create a dummy event to send around
        final Event event = new Event(Long.class, Long.valueOf(42));
        event.setId(new EventId(new ParticipantId(), 7));
        event.setScope(new Scope("/test/"));
        final Builder builder = Notification.newBuilder();
        builder.setEventId(ProtocolConversion.createEventIdBuilder(event
                .getId()));
        final Converter<ByteBuffer> converter =
                new LongConverter(LongConverter.UINT64_SIGNATURE);
        final ByteBuffer serialization =
                converter.serialize(event.getType(), event.getData())
                        .getSerialization();
        builder.setData(ByteHelpers.buteBufferToByteString(serialization));

        ProtocolConversion.fillNotificationHeader(builder, event, converter
                .getSignature().getSchema());

        return builder.build();

    }

}

// CHECKSTYLE.ON: JavadocMethod
// CHECKSTYLE.ON: MagicNumber
