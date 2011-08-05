package rsb.patterns;

import java.util.Collection;
import java.util.HashMap;

import rsb.Participant;
import rsb.Scope;
import rsb.transport.PortConfiguration;
import rsb.transport.TransportFactory;

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
	
	protected class ServerStateActive extends ServerState {
		protected ServerStateActive(Server ctx) {
			super(ctx);
		}

		public ServerState deactivate() {
			for (Method method : methods.values()) {
				method.deactivate();
			}
			return new ServerStateInactive(s);
		}
	}

	protected class ServerStateInactive extends ServerState {
		
		protected ServerStateInactive(Server ctx) {
			super(ctx);
		}

		public ServerState activate() {
			return new ServerStateActive(s);
		}
	}

	private HashMap<String, Method> methods;
	private ServerState state;

	protected Server(Scope scope, TransportFactory transportFactory,
			PortConfiguration portConfig) {
		super(scope, transportFactory, portConfig);
		methods = new HashMap<String, Method>();
		state = new ServerStateInactive(this);
	}

	/**
	 * Return all methods of the server.
	 * 
	 * @return A Collection containing all methods.
	 */
	public Collection<Method> getMethods() {
		return this.methods.values();
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