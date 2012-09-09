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
package rsb.converter;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class ProtocolBufferConverter<MessageType extends Message> implements Converter<ByteBuffer> {

	final static Logger LOG = Logger.getLogger(ProtocolBufferConverter.class.getName());

	MessageType defaultInstance;
	ConverterSignature signature;

	public ProtocolBufferConverter(MessageType instance) {
		defaultInstance = instance;
		LOG.fine("Result of instance.getClass().getName() " + instance.getClass().getName());
		signature = new ConverterSignature(getWireSchema(),instance.getClass());
	}

	@Override
	public WireContents<ByteBuffer> serialize(
			Class<?> typeInfo, Object obj) throws ConversionException {
		@SuppressWarnings("unchecked")
		ByteBuffer serialized = ByteBuffer.wrap(((MessageType) obj).toByteArray());
		return new WireContents<ByteBuffer>(serialized,getWireSchema());
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserData<MessageType> deserialize(String wireSchema,
			ByteBuffer buffer) throws ConversionException {
		assert(wireSchema.contentEquals(getWireSchema()));

		MessageType result;
		try {
			result = (MessageType) defaultInstance.newBuilderForType().mergeFrom(buffer.array()).build();
		} catch (InvalidProtocolBufferException e) {
			throw new ConversionException(
					"Error deserializing wire data because of a protobuf problem.",
					e);
		}
		return new UserData<MessageType>(result, result.getClass());
	}

	private String getWireSchema() {
		LOG.fine("Detected wire type: " + defaultInstance.getDescriptorForType().getFullName());
		return "." + defaultInstance.getDescriptorForType().getFullName();
	}

	@Override
	public ConverterSignature getSignature() {
		return signature;
	}

}
