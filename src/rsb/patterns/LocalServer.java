package rsb.patterns;

import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.Scope;
import rsb.filter.Filter;
import rsb.filter.MethodFilter;
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
		LOG.info("Registering new method " + name + " with signature object: " + callback);
		LocalMethod method = new LocalMethod(this, name);
		
		// filter for request method
		Filter filter = new MethodFilter("REQUEST");
		method.getListener().addFilter(filter);
		
		// handler for invoking user-supplied callback
		RequestHandler<U, T> handler = new RequestHandler<U, T>(method, callback);

		// TODO check on duplicates!!!
		// TODO at least throw a warning if a method already exists
		method.getListener().addHandler(handler, false);
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