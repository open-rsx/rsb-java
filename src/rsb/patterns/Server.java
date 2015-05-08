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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rsb.Participant;
import rsb.ParticipantCreateArgs;
import rsb.RSBException;

/**
 * Objects of this class represent local or remote serves. A server is basically
 * a collection of named methods that are bound to a specific scope.
 *
 * This class is primarily intended as a superclass for local and remote server
 * classes.
 *
 * @author jmoringe
 * @param <MethodType>
 *            The type of methods used in the subclasses
 */
public abstract class Server<MethodType extends Method> extends Participant {

    private final Map<String, MethodType> methods;
    private ServerState state;

    /**
     * Abstract base class for implementations of the different server states.
     */
    private abstract class ServerState {

        // reference to server instance
        private Server<MethodType> server;

        protected ServerState(final Server<MethodType> server) {
            this.setServer(server);
        }

        /**
         * Activates this state.
         *
         * @return the state that should follow this operation
         * @throws IllegalStateException
         *             server in incorrect state
         * @throws rsb.InitializeException
         *             error initializing the server
         * @throws RSBException
         *             error initializing the server
         */
        public ServerState activate() throws RSBException {
            throw new IllegalStateException("Server already activated.");
        }

        /**
         * Deactivates this state.
         *
         * @return the state that should follow this operation
         * @throws IllegalStateException
         *             server in incorrect state
         * @throws RSBException
         *             error initializing the server
         * @throws InterruptedException
         *             interrupted while waiting for deactivation to finish
         */
        public ServerState deactivate() throws RSBException,
                InterruptedException {
            throw new IllegalStateException("Server not activated.");
        }

        public void run(@SuppressWarnings("unused") final boolean async) {
            throw new IllegalStateException("server not activated");
        }

        public abstract boolean isActive();

        protected Server<MethodType> getServer() {
            return this.server;
        }

        protected void setServer(final Server<MethodType> server) {
            this.server = server;
        }

    }

    /**
     * Represent an active server.
     */
    protected class ServerStateActive extends ServerState {

        /**
         * Constructor.
         *
         * @param server
         *            server managed by this state
         */
        ServerStateActive(final Server<MethodType> server) {
            super(server);
        }

        @Override
        public ServerState deactivate() throws RSBException,
                InterruptedException {
            Server.super.deactivate();
            for (final Method method : Server.this.methods.values()) {
                method.deactivate();
            }
            // send signal to thread in waitForShutdown
            synchronized (this.getServer()) {
                this.getServer().notifyAll();
            }
            return new ServerStateInactive(this.getServer());
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    /**
     * Represents an inactive server.
     *
     * @author jwienke
     */
    protected class ServerStateInactive extends ServerState {

        /**
         * Constructor.
         *
         * @param server
         *            server managed by this state
         */
        ServerStateInactive(final Server<MethodType> server) {
            super(server);
        }

        @Override
        public ServerState activate() throws RSBException {
            for (final Method method : Server.this.methods.values()) {
                method.activate();
            }
            Server.super.activate();
            return new ServerStateActive(this.getServer());
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }

    /**
     * Constructs a new server.
     *
     * @param args
     *            arguments used for this server
     */
    protected Server(final ParticipantCreateArgs<?> args) {
        super(args);
        this.methods = new HashMap<String, MethodType>();
        this.state = new ServerStateInactive(this);
    }

    /**
     * Return all methods of the server.
     *
     * @return A Collection containing all methods.
     */
    public Collection<MethodType> getMethods() {
        synchronized (this) {
            return new ArrayList<MethodType>(this.methods.values());
        }
    }

    /**
     * Returns the method with the given name.
     *
     * @param name
     *            method name
     * @return {@link Method} instance or <code>null</code> if no method exists
     *         with this name
     */
    public MethodType getMethod(final String name) {
        synchronized (this) {
            return this.methods.get(name);
        }
    }

    /**
     * Indicates whether a method with the given name is already registered.
     *
     * @param name
     *            name of the method
     * @return <code>true</code> if a method is registered with the given name,
     *         else <code>false</code>
     */
    public boolean hasMethod(final String name) {
        synchronized (this) {
            return this.methods.containsKey(name);
        }
    }

    /**
     * Adds a method to the server.
     *
     * @param name
     *            name under which the method should be registered
     * @param method
     *            the method instance
     * @param overwrite
     *            if <code>true</code>, overwrite an existing method with that
     *            name, else raise an exception
     * @throws IllegalArgumentException
     *             method with the given name already exists and shall not be
     *             overwritten
     */
    protected void addMethod(final String name, final MethodType method,
            final boolean overwrite) {
        assert name != null;
        assert method != null;
        synchronized (this) {
            if (this.methods.containsKey(name) && !overwrite) {
                throw new IllegalArgumentException("A method with name " + name
                        + " already exists.");
            }
            method.setObserverManager(getObserverManager());
            this.methods.put(name, method);
        }
    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.state.isActive();
        }
    }

    @Override
    public void activate() throws RSBException {
        synchronized (this) {
            this.state = this.state.activate();
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        synchronized (this) {
            this.state = this.state.deactivate();
        }
    }

};
