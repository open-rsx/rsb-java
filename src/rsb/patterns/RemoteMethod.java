package rsb.patterns;

/**
 * Objects of this class represent methods provided by a remote
 * server.
 *
 * @author jmoringe
 */
public class RemoteMethod<U, T> extends Method {
	
	/**
	 * Create a new RemoteMethod object that represent the remote method named @a
	 * name provided by @a server.
	 * 
	 * @param server
	 *            The remote server providing the method.
	 * @param name
	 *            The name of the method.
	 */
	public RemoteMethod(Server server, String name) {
		super(server, name);
		listener = factory.createListener(REPLY_SCOPE);
		informer = factory.createInformer(REQUEST_SCOPE);			
	}

	public U call(T request) {
		return (U) null;
	}
};