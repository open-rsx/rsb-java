package rsb.converter;

import java.nio.ByteBuffer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class ProtocolBufferConverter<MessageType extends Message> implements Converter<ByteBuffer> {

	MessageType defaultInstance;
	
	public ProtocolBufferConverter(MessageType instance) {
		defaultInstance = instance;
	}
	
	@Override
	public WireContents<ByteBuffer> serialize(
			String typeInfo, Object obj) throws ConversionException {
		@SuppressWarnings("unchecked")
		ByteBuffer serialized = ByteBuffer.wrap(((MessageType) obj).toByteArray()); 
		return new WireContents<ByteBuffer>(serialized,getWireSchema());
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserData deserialize(String wireSchema,
			ByteBuffer buffer) throws ConversionException {
		assert(wireSchema == getWireSchema());
		
		MessageType result;
		try {
			result = (MessageType) defaultInstance.newBuilderForType().mergeFrom(buffer.array()).build();
		} catch (InvalidProtocolBufferException e) {
			throw new ConversionException(
					"Error deserializing wire data because of a protobuf problem.",
					e);
		}
		return new UserData(result, result.getClass().getName());
	}
	
	private String getWireSchema() {
		return defaultInstance.getDescriptorForType().getFullName();
	}

}
