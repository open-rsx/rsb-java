package rsb.patterns;

import rsb.Scope;
import rsb.InitializeException;

import rsb.transport.TransportFactory;
import rsb.transport.PortConfiguration;

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
    public LocalServer(Scope scope) {
	super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
    }

    public void addMethod(String name, DataCallback<?, ?> callback) throws InitializeException {
	new LocalMethod(this, name);
    }

};