package rsb.patterns;

import java.util.HashMap;

import rsb.RSBException;
import rsb.InvalidStateException;
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

    protected class ServerState {
	public ServerState activate() throws InvalidStateException {
	    throw new InvalidStateException("Server already activated.");
	}

	public ServerState deactivate() throws InvalidStateException {
	    throw new InvalidStateException("Server not activated.");
	}
    }

    protected class ServerStateActive extends ServerState {
	public ServerState deactivate() {
	    for (Method method : methods.values()) {
		method.deactivate();
	    }
	    return new ServerStateInactive();
	}
    }

    protected class ServerStateInactive extends ServerState {
	public ServerState activate() {
	    return new ServerStateActive();
	}
    }

    private ServerState state;
    private HashMap<String, Method> methods;

    protected Server(Scope	       scope,
		     TransportFactory  transportFactory,
		     PortConfiguration portConfig) {
	super(scope, transportFactory, portConfig);
	state = new ServerStateInactive();
    }

    @Override
    public boolean isActive() {
	return state.getClass() == ServerStateActive.class;
    }

    @Override
    public void activate() {
	state = state.activate();
    }

    @Override
    public void deactivate() {
	state = state.deactivate();
    }

};