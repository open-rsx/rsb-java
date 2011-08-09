package rsb.patterns;

import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.Scope;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

/**
 * Objects of this class associate a collection of method objects
 * which are implemented by callback functions with a scope under
 * which these methods are exposed for remote clients.
 *
 * @author jmoringe
 */
public class LocalServer extends Server {

	private final static Logger LOG = Logger.getLogger(LocalServer.class.getName());
	
	/**
     * Create a new LocalServer object that exposes its methods under
     * the scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of
     *            the newly created server should be provided.
     */
	public LocalServer(final Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
	}

	public <U, T> void addMethod(String name, DataCallback<U, T> callback)
			throws InitializeException {
		LOG.fine("Registering new data method " + name + " with signature object: " + callback);
		LocalMethod<U, T> method = new LocalMethod<U, T>(this, name,callback);
		addAndActivate(name, method);
	}
	
	
	public void addMethod(String name, EventCallback callback) throws InitializeException {
		LOG.fine("Registering new event method " + name + " with signature object: " + callback);
		LocalMethod<Event, Event> method = new LocalMethod<Event, Event>(this, name, callback);
		addAndActivate(name, method);
	}	

	/**
	 * @param name
	 * @param method
	 * @throws InitializeException
	 */
	private <U, T> void addAndActivate(String name, LocalMethod<?, ?> method)
			throws InitializeException {
		if (methods.containsKey(name)) {
			LOG.warning("Method with name " + name + " already registered. Overwriting it!");
		}
		methods.put(name, method);
		
		if (this.isActive()) {
			method.activate();
		}
	}
	
	public synchronized void waitForShutdown() {
		// Blocks calling thread as long as this Server instance
		// is in activated state
		if (isActive()) {
			try {
				// Wait until we are done
				this.wait();
			} catch (InterruptedException ex) {
				// Server has been deactivated, return from run
			}
		}
	}

};