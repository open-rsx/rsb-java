package rsb.patterns;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import rsb.Event;
import rsb.InitializeException;
import rsb.RSBException;
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
		super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
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
		super(scope, TransportFactory.getInstance(), PortConfiguration.NONE);
		this.timeout = 25;
	}
	
	public double getTimeout() {
		return timeout;
	}

	public Event call(final String name, final Event event) {
		return null;
	}	
	
	public Future<Event> callAsync(final String name, final Event event) {
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	/**
	 * Async call returning an rsb.patterns.Future object
	 * 
	 * @param name
	 * @param data
	 * @return
	 * @throws RSBException
	 */
	public <U, T> Future<U> callAsync(final String name, final T data) throws RSBException {
		RemoteMethod<U, T> method = null;
		// get method, either new or cached
		if (!methods.containsKey(name)) {
			try {
				method = addMethod(name);
			} catch (InitializeException exception) {
				LOG.warning("Exception during method activation: " + exception.getMessage() + " Re-throwing it.");
				throw new RSBException(exception);
			}
		} else {
			try {
				method = (RemoteMethod<U, T>) methods.get(name);
			} catch (ClassCastException exception) {
				LOG.warning("Exception during method activation: " + exception.getMessage() + " Re-throwing it.");
				throw new RSBException(exception);
			}
		}
		return method.callAsync(data);
	}
	
	/**
	 * Blocking call directly returning the data or throwing an 
	 * exception upon timeout, interruption or failure.
	 * 
	 * @param name
	 * @param data
	 * @return
	 * @throws RSBException
	 */
	public <U, T> U call(final String name, final T data) throws RSBException {
		Future<U> future = callAsync(name, data);
		U result;
		try {
			result = future.get((long) timeout,TimeUnit.SECONDS);
		} catch (InterruptedException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		} catch (ExecutionException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		} catch (TimeoutException exception) {
			LOG.warning("Exception during remote call: " + exception.getMessage() + " Re-throwing it.");
			throw new RSBException(exception);
		}
		return result;
	}

	protected <U, T> RemoteMethod<U, T> addMethod(String name)
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