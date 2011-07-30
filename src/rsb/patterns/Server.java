package rsb.patterns;

import java.util.HashMap;

import rsb.RSBException;
import rsb.Scope;
import rsb.Participant;

import rsb.transport.TransportFactory;
import rsb.transport.PortConfiguration;

/**
 * Objects of this class represent local or remote serves. A server is
 * basically a collection of named methods that are bound to a
 * specific scope.
 *
 * This class is primarily intended as a superclass for local and
 * remote server classes.
 *
 * @author jmoringe
 */
public abstract class Server extends Participant {

    private HashMap<String, Method> methods;

    protected Server(Scope	       scope,
		     TransportFactory  transportFactory,
		     PortConfiguration portConfig) {
	super(scope, transportFactory, portConfig);
    }

    public void deactivate() throws RSBException {
	for (Method method : methods.values()) {
	    method.deactivate();
	}
    }

};