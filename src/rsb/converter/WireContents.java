package rsb.converter;

/**
 * A wrapper around the contents to be placed on the wire.
 * 
 * @author jwienke
 * @param <WireType>
 *            serialization type of the wire contents
 */
public class WireContents<WireType> {

	private WireType serialization;
	private String wireSchema;

	/**
	 * Constructs a new wrapper around serialized data.
	 * 
	 * @param serialization
	 *            the serialized data
	 * @param wireSchema
	 *            the wire schema identifier of the serialized data
	 */
	public WireContents(WireType serialization, String wireSchema) {
		this.serialization = serialization;
		this.wireSchema = wireSchema;
	}

	/**
	 * Returns the contents for the wire in their serialized form.
	 * 
	 * @return serialized contents
	 */
	public WireType getSerialization() {
		return serialization;
	}

	/**
	 * Returns the identifier of the wire schema that was used to serialize
	 * the contents.
	 * 
	 * @return wire schema identifier
	 */
	public String getWireSchema() {
		return wireSchema;
	}

}