/**
 * ============================================================
 *
 * This file is part of the RSBJava project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation;
 * either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * ============================================================
 */
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