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
package rsb.protocol;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import rsb.Event;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.NoSuchConverterException;
import rsb.converter.UserData;
import rsb.converter.WireContents;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.EventMetaDataType.EventMetaData;
import rsb.protocol.EventMetaDataType.UserInfo;
import rsb.protocol.EventMetaDataType.UserTime;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;

import com.google.protobuf.ByteString;

/**
 * A utility class to converter between protocol buffers based classes and our
 * user level types.
 *
 * @author jwienke
 */
public final class ProtocolConversion {

    private static final Logger LOG = Logger.getLogger(ProtocolConversion.class
            .getName());

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

    /**
     * Serializes the payload contained in an {@link Event} instance using a
     * specified {@link ConverterSelectionStrategy}.
     *
     * @param event
     *            event containing the data to serialize
     * @param converters
     *            the converters to use
     * @return the serialized data including the generated wire schema
     * @throws ConversionException
     *             error converting, e.g. no converter available or wrong class
     */
    public static WireContents<ByteBuffer> serializeEventData(
            final Event event,
            final ConverterSelectionStrategy<ByteBuffer> converters)
            throws ConversionException {
        try {
            final Converter<ByteBuffer> converter = converters
                    .getConverter(event.getType().getName());
            final WireContents<ByteBuffer> convertedDataBuffer = converter
                    .serialize(event.getType(), event.getData());
            return convertedDataBuffer;
        } catch (final NoSuchConverterException e) {
            throw new ConversionException(e);
        }
    }

    /**
     * Build event from RSB Notification. Excludes user data de-serialization as
     * it is bound to the converter configuration.
     *
     * @param notification
     *            {@link rsb.protocol.NotificationType.Notification} instance
     *            to deserialize
     * @return deserialized {@link Event} instance
     */
    public static Event fromNotification(final Notification notification) {
        LOG.fine("decoding notification");
        final Event event = new Event();
        event.setScope(new Scope(notification.getScope().toStringUtf8()));
        event.setId(new ParticipantId(notification.getEventId().getSenderId()
                .toByteArray()), notification.getEventId().getSequenceNumber());
        if (notification.hasMethod()) {
            event.setMethod(notification.getMethod().toStringUtf8());
        }

        LOG.finest("returning event with id: " + event.getId());

        // metadata
        event.getMetaData().setCreateTime(
                notification.getMetaData().getCreateTime());
        event.getMetaData().setSendTime(
                notification.getMetaData().getSendTime());
        event.getMetaData().setReceiveTime(0);
        for (final UserInfo info : notification.getMetaData()
                .getUserInfosList()) {
            event.getMetaData().setUserInfo(info.getKey().toStringUtf8(),
                    info.getValue().toStringUtf8());
        }
        for (final UserTime time : notification.getMetaData()
                .getUserTimesList()) {
            event.getMetaData().setUserTime(time.getKey().toStringUtf8(),
                    time.getTimestamp());
        }

        // causes
        for (final EventId cause : notification.getCausesList()) {
            event.addCause(new rsb.EventId(new ParticipantId(cause // NOPMD
                    .getSenderId().toByteArray()), cause.getSequenceNumber()));
        }

        return event;
    }

    /**
     * Builds an {@link Event} instance from a
     * {@link rsb.protocol.NotificationType.Notification} and a
     * {@link ByteBuffer} containing the event payload. This includes the
     * deserialization of the payload using a specified converter strategy.
     *
     * @param notification
     *            the notification with the event meta data
     * @param serializedData
     *            the serialized data
     * @param converters
     *            converter strategy to use for deserializing the data
     * @return the constructed and complete event including the payload
     * @throws ConversionException
     *             unable to deserialize the data with the specified converter
     *             stategy
     */
    public static Event fromNotification(final Notification notification,
            final ByteBuffer serializedData,
            final ConverterSelectionStrategy<ByteBuffer> converters)
            throws ConversionException {

        final Event resultEvent = fromNotification(notification);

        final Converter<ByteBuffer> converter = converters
                .getConverter(notification.getWireSchema().toStringUtf8());
        final UserData<?> userData = converter.deserialize(notification
                .getWireSchema().toStringUtf8(), serializedData);
        resultEvent.setData(userData.getData());
        resultEvent.setType(userData.getTypeInfo());

        return resultEvent;

    }

}
