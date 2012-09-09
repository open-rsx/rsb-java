/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
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
