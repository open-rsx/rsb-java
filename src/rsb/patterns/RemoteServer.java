package rsb.patterns;

import rsb.Scope;

import rsb.transport.TransportFactory;
import rsb.transport.PortConfiguration;

/**
 * Objects of this class represent remote servers in a way that allows
 * calling methods on them as if they were local.
 *
 * @author jmoringe
 */
public class RemoteServer extends Server {

    private double timeout;

    /**
     * Create a new RemoteServer object that provides its methods
     * under the scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of
     *            the remote created server are provided.
     * @param timeout
     *            The amount of seconds methods calls should wait for
     *            their replies to arrive before failing.
     */
    public RemoteServer(Scope  scope, double timeout) {
	super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
	this.timeout = timeout;
    }

    /**
     * Create a new RemoteServer object that provides its methods
     * under the scope @a scope.
     *
     * @param scope
     *            The common super-scope under which the methods of
     *            the remote created server are provided.
     */
    public RemoteServer(Scope  scope) {
	super(scope, TransportFactory.getInstance(), PortConfiguration.IN);
	this.timeout = 25;
    }


    public double getTimeout() {
	return timeout;
    }

};