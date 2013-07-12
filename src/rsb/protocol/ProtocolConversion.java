package rsb.protocol;

import rsb.Event;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.EventMetaData;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.NotificationType.Notification.Builder;

import com.google.protobuf.ByteString;

/**
 * A utility class to converter between protocol buffers based classes and our
 * user level types.
 *
 * @author jwienke
 */
public final class ProtocolConversion {

    private ProtocolConversion() {
        super();
        // prevent instantiation of a helper class
    }

    /**
     * Creates a build for the serialization of an {@link rsb.EventId}, filled
     * with the contents of the given id.
     *
     * @param id
     *            the id to serialize
     * @return the builder
     */
    public static EventId.Builder createEventIdBuilder(
            @SuppressWarnings("PMD.ShortVariable") final rsb.EventId id) {
        final EventId.Builder eventIdBuilder = EventId.newBuilder();
        eventIdBuilder.setSenderId(ByteString.copyFrom(id.getParticipantId()
                .toByteArray()));
        eventIdBuilder.setSequenceNumber((int) id.getSequenceNumber());
        return eventIdBuilder;
    }

    /**
     * Fills the notification header in a builder instance.
     *
     * @param notificationBuilder
     *            the builder where fields should be filled
     * @param event
     *            the event to serialize and to greb the data for the
     *            notification header from
     * @param wireSchema
     *            the wire shema of the serialized data
     */
    public static void fillNotificationHeader(
            final Builder notificationBuilder, final Event event,
            final String wireSchema) {

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
            notificationBuilder.addCauses(ProtocolConversion
                    .createEventIdBuilder(cause));
        }

    }

}
