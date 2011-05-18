package rsb.transport;

import rsb.RSBException;

public class SerializeException extends RSBException {

	private static final long serialVersionUID = -6286452461407036752L;

	public SerializeException(String msg) {
		super(msg);
	}

	public SerializeException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
