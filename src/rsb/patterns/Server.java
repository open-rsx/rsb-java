/**
 * ============================================================
 *
 * This file is part of the RSBJava project
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rsb.InitializeException;
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
		protected ServerStateActive(final Server ctx) {
			super(ctx);
		}

		public ServerState deactivate() {
			for (Method method : methods.values()) {
				method.deactivate();
			}
			server.getRouter().deactivate();
			// send signal to thread in waitForShutdown
			synchronized (server) {
				server.notify();
			}
			return new ServerStateInactive(server);
		}
	}

	protected class ServerStateInactive extends ServerState {

		protected ServerStateInactive(final Server ctx) {
			super(ctx);
		}

		public ServerState activate() throws InitializeException {
			for (Method method : methods.values()) {
				method.activate();
			}
			return new ServerStateActive(server);
		}
	}


	protected final Map<String, Method> methods;
	private ServerState state;

	protected Server(final Scope scope, final TransportFactory transportFactory,
			final PortConfiguration portConfig) {
		super(scope, transportFactory, portConfig);
		methods = new HashMap<String, Method>();
		state = new ServerStateInactive(this);
	}
	
	protected Server(final String scope, final TransportFactory transportFactory,
			final PortConfiguration portConfig) {
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
	public void activate() throws InitializeException {
		state = state.activate();
	}

	@Override
	public void deactivate() {
		state = state.deactivate();
	}

};