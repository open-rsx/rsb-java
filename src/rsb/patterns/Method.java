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

import java.util.logging.Logger;

import rsb.Factory;
import rsb.InitializeException;
import rsb.InvalidStateException;
import rsb.RSBObject;
import rsb.Listener;
import rsb.Informer;
import rsb.Scope;

/**
 * Objects of this class are methods which are associated to a local or remote
 * server. Within a server, each method has a unique name.
 *
 * This class is primarily intended as a superclass for local and remote method
 * classes.
 *
 * @author jmoringe
 * @author swrede
 */
public abstract class Method implements RSBObject {

	protected final static Logger LOG = Logger.getLogger(Method.class.getName());

	protected class MethodState {
		public MethodState activate() throws InitializeException {
			throw new InvalidStateException("Method already activated.");
		}

		public MethodState deactivate() {
			throw new InvalidStateException("Method not activated.");
		}
	}

	protected class MethodStateActive extends MethodState {
		public MethodState deactivate() {
			// Deactivate informer and listener if necessary.
			if (listener != null) {
				listener.deactivate();
				listener = null;
			}
			if (informer != null) {
				informer.deactivate();
				informer = null;
			}
			return new MethodStateInactive();
		}
	}

	protected class MethodStateInactive extends MethodState {
		public MethodState activate() throws InitializeException {
			listener.activate();
			informer.activate();
			return new MethodStateActive();
		}
	}

	protected Factory factory;
	private Server server;
	private String name;
	protected Informer<?> informer;
	protected Listener listener;
	private MethodState state;
	protected final Scope REQUEST_SCOPE;
	protected final Scope REPLY_SCOPE;

	/**
	 * Create a new Method object for the method named @a name provided by @a
	 * server.
	 *
	 * @param server
	 *            The remote or local server to which the method is associated.
	 * @param name
	 *            The name of the method. Unique within a server.
	 */
	public Method(Server server, String name) {
		this.server = server;
		this.name = name;
		// TODO make sure that case doesn't matter (generally!)
		this.REQUEST_SCOPE = server.getScope().concat(new Scope("/request"+"/"+name));
		this.REPLY_SCOPE = server.getScope().concat(new Scope("/reply"+"/"+name));
		this.factory = Factory.getInstance();
		this.state = new MethodStateInactive();
	}

	/**
	 * Return the Server object to which this method is associated.
	 *
	 * @return The Server object.
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Return the name of this method.
	 *
	 * @return The name of this method.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the Informer object associated to this method.
	 *
	 * The Informer object may be created lazily.
	 *
	 * @return The Informer object.
	 */
	public Informer<?> getInformer() {
		return informer;
	}

	/**
	 * Return the Listener object associated to this method.
	 *
	 * The Listener object may be created lazily.
	 *
	 * @return The Listener object.
	 */
	public Listener getListener() {
		return listener;
	}

	@Override
	public boolean isActive() {
		return state.getClass() == MethodStateActive.class;
	}

	@Override
	public void activate() throws InitializeException {
		state = state.activate();
	}

	@Override
	public void deactivate() {
		state = state.deactivate();
	}

	@Override
	public String toString() {
		return "Method[name=" + getName() + "]";
	}

};