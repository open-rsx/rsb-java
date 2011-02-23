package rsb.xml;

public class SchemaNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2746977341273609362L;

	public SchemaNotFoundException(String message) {
		super(message);
	}

	public SchemaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SchemaNotFoundException(Throwable cause) {
		super(cause);
	}	
	
}
