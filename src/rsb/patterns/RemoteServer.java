package rsb.patterns;

import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.Scope;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

/**
 * Objects of this class represent remote servers in a way that allows
 * calling methods on them as if they were local.
 *
 * @author jmoringe
 * @author swrede
 * 
 */
public class RemoteServer extends Server {

	private static final Logger LOG = Logger.getLogger(RemoteServer.class.getName());
	
    private double timeout;

	/**
	 * Create a new RemoteServer object that provides its methods under the
	 * scope @a scope.
	 * 
	 * @param scope
	 *            The common super-scope under which the methods of the remote
	 *            created server are provided.
	 * @param timeout
	 *            The amount of seconds methods calls should wait for their
	 *            replies to arrive before failing.
	 */
	public RemoteServer(Scope scope, double timeout) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
		this.timeout = timeout;
	}

	/**
	 * Create a new RemoteServer object that provides its methods under the
	 * scope @a scope.
	 * 
	 * @param scope
	 *            The common super-scope under which the methods of the remote
	 *            created server are provided.
	 */
	public RemoteServer(Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
		this.timeout = 25;
	}
	
	public double getTimeout() {
		return timeout;
	}

	public <U, T> RemoteMethod<U,T> addMethod(String name)
			throws InitializeException {
		LOG.info("Registering new method " + name);
		RemoteMethod<U, T> method = new RemoteMethod<U, T>(this, name);
		methods.put(name, method);
		
		if (this.isActive()) {
			method.activate();
		}
		
		return method;
	}	
	
};