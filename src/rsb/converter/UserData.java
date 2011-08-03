package rsb.converter;

/**
 * A wrapper around deserialized data that contains the unspecific
 * {@link Object} instance with a class object describing its type.
 * 
 * @author jwienke
 * @author swrede
 */
public class UserData<WireType> {

	private Object data;	
	private Class<?> typeInfo;

	public UserData(Object data, Class<?> typeInfo) {
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
	 * Class object describing the type of the deserialized data.
	 * 
	 * @return class type info
	 */
	public Class<?> getTypeInfo() {
		return typeInfo;
	}

}