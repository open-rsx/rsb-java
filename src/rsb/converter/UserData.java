package rsb.converter;

/**
 * A wrapper around deserialized data that contains the unspecific
 * {@link Object} instance with a string describing its type.
 * 
 * @author jwienke
 */
public class UserData<WireType> {

	private Object data;
	private String typeInfo;

	public UserData(Object data, String typeInfo) {
		this.data = data;
		this.typeInfo = typeInfo;
	}

	/**
	 * Returns the deserialized data.
	 * 
	 * @return deserialized data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * String describing the type of the deserialized data.
	 * 
	 * @return string type info
	 */
	public String getTypeInfo() {
		return typeInfo;
	}

}