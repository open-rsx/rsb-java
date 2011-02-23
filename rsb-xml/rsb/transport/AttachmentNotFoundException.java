package rsb.transport;

public class AttachmentNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5980624739275051555L;

	public AttachmentNotFoundException(String message) {
		super(message);
	}

	public AttachmentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttachmentNotFoundException(Throwable cause) {
		super(cause);
	}	
	
}
