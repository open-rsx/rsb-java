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
import rsb.converter.Uint64Converter;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.protocol.ProtocolConversion;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

import com.google.protobuf.ByteString;

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
        final Converter<ByteBuffer> converter = new Uint64Converter();
        final ByteBuffer serialization = converter.serialize(event.getType(),
                event.getData()).getSerialization();
        builder.setData(ByteString.copyFrom(serialization.array(), 0,
                serialization.limit()));

        ProtocolConversion.fillNotificationHeader(builder, event, converter
                .getSignature().getSchema());

        return builder.build();

    }

}
