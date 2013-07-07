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

import rsb.Factory;
import rsb.Informer;
import rsb.InitializeException;
import rsb.InvalidStateException;
import rsb.Listener;
import rsb.RSBObject;
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

    protected final static Logger LOG = Logger
            .getLogger(Method.class.getName());

    protected Factory factory;
    private final Server<?> server;
    private final String name;
    protected Informer<?> informer;
    protected Listener listener;
    private MethodState state;
    protected final Scope REQUEST_SCOPE;
    protected final Scope REPLY_SCOPE;

    protected class MethodState {

        public MethodState activate() throws InitializeException {
            throw new InvalidStateException("Method already activated.");
        }

        public MethodState deactivate() {
            throw new InvalidStateException("Method not activated.");
        }
    }

    protected class MethodStateActive extends MethodState {

        @Override
        public MethodState deactivate() {
            // Deactivate informer and listener if necessary.
            if (Method.this.listener != null) {
                Method.this.listener.deactivate();
                Method.this.listener = null;
            }
            if (Method.this.informer != null) {
                Method.this.informer.deactivate();
                Method.this.informer = null;
            }
            return new MethodStateInactive();
        }
    }

    protected class MethodStateInactive extends MethodState {

        @Override
        public MethodState activate() throws InitializeException {
            Method.this.listener.activate();
            Method.this.informer.activate();
            return new MethodStateActive();
        }
    }

    /**
     * Create a new Method object for the method named @a name provided by @a
     * server.
     * 
     * @param server
     *            The remote or local server to which the method is associated.
     * @param name
     *            The name of the method. Unique within a server.
     */
    public Method(final Server<?> server, final String name) {
        this.server = server;
        this.name = name;
        // TODO make sure that case doesn't matter (generally!)
        this.REQUEST_SCOPE = server.getScope().concat(
                new Scope("/request" + "/" + name));
        this.REPLY_SCOPE = server.getScope().concat(
                new Scope("/reply" + "/" + name));
        this.factory = Factory.getInstance();
        this.state = new MethodStateInactive();
    }

    /**
     * Return the Server object to which this method is associated.
     * 
     * @return The Server object.
     */
    public Server<?> getServer() {
        return this.server;
    }

    /**
     * Return the name of this method.
     * 
     * @return The name of this method.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the Informer object associated to this method.
     * 
     * The Informer object may be created lazily.
     * 
     * @return The Informer object.
     */
    public Informer<?> getInformer() {
        return this.informer;
    }

    /**
     * Return the Listener object associated to this method.
     * 
     * The Listener object may be created lazily.
     * 
     * @return The Listener object.
     */
    public Listener getListener() {
        return this.listener;
    }

    @Override
    public boolean isActive() {
        return this.state.getClass() == MethodStateActive.class;
    }

    @Override
    public void activate() throws InitializeException {
        this.state = this.state.activate();
    }

    @Override
    public void deactivate() {
        this.state = this.state.deactivate();
    }

    @Override
    public String toString() {
        return "Method[name=" + this.getName() + "]";
    }

};
