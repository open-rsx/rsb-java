package rsb.patterns;

import java.util.logging.Logger;

import rsb.InvalidStateException;

/**
 * Abstract base class for implementations
 * of the different server states.
 * 
 */
class ServerState {
	
	protected final static Logger LOG = Logger.getLogger(ServerState.class.getName());

	// reference to server instance
	protected Server s; 
	
	protected ServerState (Server ctx) {
		s = ctx;
	}	
	
	public ServerState activate() throws InvalidStateException {
		throw new InvalidStateException("Server already activated.");
	}

	public ServerState deactivate() throws InvalidStateException {
		throw new InvalidStateException("Server not activated.");
	}
	
	public synchronized void run(boolean async) {
		throw new InvalidStateException("server not activated");
	}	
}