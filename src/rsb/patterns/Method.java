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

import rsb.Activatable;
import rsb.Factory;
import rsb.Informer;
import rsb.InvalidStateException;
import rsb.Listener;
import rsb.RSBException;
import rsb.Scope;

/**
 * Objects of this class are methods which are associated to a local or remote
 * server. Within a server, each method has a unique name.
 *
 * This class is primarily intended as a superclass for local and remote method
 * classes.
 *
 * This class manages the {@link Listener} and {@link Informer} instances used
 * to communicate between methods as well as a state pattern for activating
 * them.
 *
 * @author jmoringe
 * @author swrede
 */
public abstract class Method implements Activatable {

    private final Factory factory;
    private final Server<?> server;
    private final String name;
    private Informer<?> informer;
    private Listener listener;
    private MethodState state;
    private final Scope requestScope;
    private final Scope replyScope;

    /**
     * Abstract base class for states of a {@link Method} based on the state
     * pattern.
     */
    protected abstract class MethodState {

        /**
         * Activates the method.
         *
         * @return the next state of the method
         * @throws rsb.InitializeException
         *             error initializing the method
         * @throws RSBException
         *             error initializing the method
         * @throws InvalidStateException
         *             method in wrong state for this operation
         */
        public MethodState activate() throws RSBException {
            throw new InvalidStateException("Method already activated.");
        }

        /**
         * Deactivates the method.
         *
         * @return next method state
         * @throws RSBException
         *             error deactivating underlying RSB objects.
         * @throws InterruptedException
         *             interrupted while waiting for the shutdown of RSB objects
         * @throws InvalidStateException
         *             method in wrong state for this operation
         */
        public MethodState deactivate() throws RSBException,
                InterruptedException {
            throw new InvalidStateException("Method not activated.");
        }
    }

    /**
     * Represents the state of a method that is currently active.
     */
    protected class MethodStateActive extends MethodState {

        @Override
        public MethodState deactivate() throws RSBException,
                InterruptedException {
            // Deactivate informer and listener if necessary.
            if (Method.this.getListener() != null) {
                Method.this.getListener().deactivate();
                Method.this.setListener(null);
            }
            if (Method.this.getInformer() != null) {
                Method.this.getInformer().deactivate();
                Method.this.setInformer(null);
            }
            return new MethodStateInactive();
        }
    }

    /**
     * Represents the state of a method that is currently inactive.
     */
    protected class MethodStateInactive extends MethodState {

        @Override
        public MethodState activate() throws RSBException {
            Method.this.getListener().activate();
            Method.this.getInformer().activate();
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
        this.requestScope =
                server.getScope().concat(new Scope("/request/" + name));
        this.replyScope = server.getScope().concat(new Scope("/reply/" + name));
        this.factory = Factory.getInstance();
        this.state = new MethodStateInactive();
    }

    /**
     * Returns the factory instance to use by this method for creating internal
     * participants.
     *
     * @return factory instance
     */
    protected Factory getFactory() {
        return this.factory;
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
    public void activate() throws RSBException {
        this.state = this.state.activate();
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.state = this.state.deactivate();
    }

    @Override
    public String toString() {
        return "Method[name=" + this.getName() + "]";
    }

    /**
     * Sets the informer instances used by this method communicate with
     * counterparts.
     *
     * @param informer
     *            the new informer instances
     */
    protected void setInformer(final Informer<?> informer) {
        this.informer = informer;
    }

    /**
     * Sets the listener instance used by this method to communicate with
     * counterparts.
     *
     * @param listener
     *            the new listeners
     */
    protected void setListener(final Listener listener) {
        this.listener = listener;
    }

    /**
     * Returns the scope on which this method issues or receives requests.
     *
     * @return scope
     */
    protected Scope getRequestScope() {
        return this.requestScope;
    }

    /**
     * Returns the scope on which this method issues or receives replies.
     *
     * @return scope
     */
    protected Scope getReplyScope() {
        return this.replyScope;
    }

};
