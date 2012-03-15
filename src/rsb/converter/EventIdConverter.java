/**
 * ============================================================
 *
 * This file is a part of the RSBJava project
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

import com.google.protobuf.ByteString;

import rsb.ParticipantId;
import rsb.protocol.EventIdType.EventId;

/**
 * A converter for {@link EventId} instances.
 * 
 * @author jwienke
 */
public class EventIdConverter implements Converter<ByteBuffer> {

	private ProtocolBufferConverter<EventId> converter = new ProtocolBufferConverter<EventId>(
			EventId.getDefaultInstance());

	@Override
	public WireContents<ByteBuffer> serialize(Class<?> typeInfo, Object obj)
			throws ConversionException {

		try {

			rsb.EventId id = (rsb.EventId) obj;

			EventId.Builder idBuilder = EventId.newBuilder();
			idBuilder.setSenderId(ByteString.copyFrom(id.getParticipantId()
					.toByteArray()));
			idBuilder.setSequenceNumber((int) id.getSequenceNumber());

			EventId builtId = idBuilder.build();
			return converter.serialize(builtId.getClass(), builtId);

		} catch (ClassCastException e) {
			throw new ConversionException(
					"Input data for serializing must be strings.", e);
		}

	}

	@Override
	public UserData<ByteBuffer> deserialize(String wireSchema, ByteBuffer buffer)
			throws ConversionException {
		EventId protocolId = (EventId) converter
				.deserialize(wireSchema, buffer).getData();
		rsb.EventId id = new rsb.EventId(new ParticipantId(protocolId
				.getSenderId().toByteArray()), protocolId.getSequenceNumber());
		return new UserData<ByteBuffer>(id, id.getClass());
	}

	@Override
	public ConverterSignature getSignature() {
		return converter.getSignature();
	}

}
