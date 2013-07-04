/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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
package rsb.converter;

import java.nio.ByteBuffer;

import rsb.ParticipantId;
import rsb.protocol.EventIdType.EventId;

import com.google.protobuf.ByteString;

/**
 * A converter for {@link EventId} instances.
 * 
 * @author jwienke
 */
public class EventIdConverter implements Converter<ByteBuffer> {

    private final ProtocolBufferConverter<EventId> converter = new ProtocolBufferConverter<EventId>(
            EventId.getDefaultInstance());

    @Override
    public WireContents<ByteBuffer> serialize(final Class<?> typeInfo,
            final Object obj) throws ConversionException {

        try {

            final rsb.EventId id = (rsb.EventId) obj;

            final EventId.Builder idBuilder = EventId.newBuilder();
            idBuilder.setSenderId(ByteString.copyFrom(id.getParticipantId()
                    .toByteArray()));
            idBuilder.setSequenceNumber((int) id.getSequenceNumber());

            final EventId builtId = idBuilder.build();
            return this.converter.serialize(builtId.getClass(), builtId);

        } catch (final ClassCastException e) {
            throw new ConversionException(
                    "Input data for serializing must be strings.", e);
        }

    }

    @Override
    public UserData<ByteBuffer> deserialize(final String wireSchema,
            final ByteBuffer buffer) throws ConversionException {
        final EventId protocolId = (EventId) this.converter.deserialize(
                wireSchema, buffer).getData();
        final rsb.EventId id = new rsb.EventId(new ParticipantId(protocolId
                .getSenderId().toByteArray()), protocolId.getSequenceNumber());
        return new UserData<ByteBuffer>(id, id.getClass());
    }

    @Override
    public ConverterSignature getSignature() {
        return this.converter.getSignature();
    }

}
