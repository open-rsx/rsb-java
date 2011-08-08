package rsb.patterns;

import rsb.Event;
import rsb.InitializeException;
import rsb.Scope;
import rsb.filter.AbstractFilter;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.filter.FilterObserver;
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

	/**
     * Create a new LocalServer object that exposes its methods under
     * the scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of
     *            the newly created server should be provided.
     */
	public LocalServer(final Scope scope) {
		super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
	}

	public void addMethod(String name, DataCallback<?, ?> callback)
			throws InitializeException {
		LocalMethod method = new LocalMethod(this, name);
		
		Filter filter = new AbstractFilter("MethodFilter") {
			
			@Override
			public void dispachToObserver(FilterObserver o, FilterAction a) {
				o.notify(this, a);
				
			}

			@Override
			public Event transform(Event e) {
				// TODO check method field
				return null;
			}
		};
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		RequestHandler<?,?> handler = new RequestHandler(method, callback);
		// TODO check on duplicates!!!
		// TODO at least throw a warning if a method already exists
		// TODO add a filter for request method set
		method.getListener().addHandler(handler, false);
		methods.put(name, method);
	}

};