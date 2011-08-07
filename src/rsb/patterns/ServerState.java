package rsb.patterns;

import java.util.logging.Logger;

import rsb.InitializeException;
import rsb.InvalidStateException;

/**
 * Abstract base class for implementations
 * of the different server states.
 * 
 */
class ServerState {
	
	protected final static Logger LOG = Logger.getLogger(ServerState.class.getName());

	// reference to server instance
	protected Server server; 
	
	protected ServerState (final Server ctx) {
		server = ctx;
	}	
	
	public ServerState activate() throws InvalidStateException, InitializeException {
		throw new InvalidStateException("Server already activated.");
	}

	public ServerState deactivate() throws InvalidStateException {
		throw new InvalidStateException("Server not activated.");
	}
	
	public synchronized void run(final boolean async) {
		throw new InvalidStateException("server not activated");
	}	
}