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
import rsb.RSBException;
import rsb.Scope;
import rsb.config.ParticipantConfig;

/**
 * Objects of this class associate a collection of method objects which are
 * implemented by callback functions with a scope under which these methods are
 * exposed for remote clients.
 *
 * @author jmoringe
 */
public class LocalServer extends Server<LocalMethod> {

    private static final Logger LOG = Logger.getLogger(LocalServer.class
            .getName());

    /**
     * Create a new LocalServer object that exposes its methods under the
     * provided scope.
     *
     * @param scope
     *            The common super-scope under which the methods of the newly
     *            created server should be provided.
     * @param config
     *            participant config for this server
     */
    public LocalServer(final Scope scope, final ParticipantConfig config) {
        super(scope, config);
    }

    /**
     * Create a new LocalServer object that exposes its methods under the scope @a
     * scope.
     *
     * @param scope
     *            The common super-scope under which the methods of the newly
     *            created server should be provided.
     * @param config
     *            participant config for this server
     */
    public LocalServer(final String scope, final ParticipantConfig config) {
        super(scope, config);
    }

    /**
     * Adds a new method to the server which can be called via
     * {@link RemoteServer} instances.
     *
     * @param name
     *            name of the method
     * @param callback
     *            callback implementing the functionality of the method
     * @throws RSBException
     *             error initializing the method
     * @throws IllegalArgumentException
     *             a method with the given name already exists.
     */
    public void addMethod(final String name, final Callback callback)
            throws RSBException {
        LOG.fine("Registering new data method " + name
                + " with signature object: " + callback);
        // TODO hook into introspection mechanism (with parent + child information)
        synchronized (this) {
            try {
                final LocalMethod method =
                        new LocalMethod(this.getScope().concat(
                                new Scope("/" + name)), callback, getConfig());
                this.addAndActivate(name, method);
            } catch (final InterruptedException e) {
                throw new InitializeException(e);
            }
        }
    }

    /**
     * Adds a method and activates it if the server is already active.
     *
     * @param name
     *            name of the method
     * @param method
     *            the method
     * @throws RSBException
     *             error initializing method
     * @throws IllegalArgumentException
     *             a method with the given name already exists.
     */
    private void addAndActivate(final String name, final LocalMethod method)
            throws RSBException {
        addMethod(name, method, false);
        if (this.isActive()) {
            method.activate();
        }
    }

    /**
     * After calling {@link #deactivate()} this methods waits until the server
     * terminated completely.
     */
    public void waitForShutdown() {
        synchronized (this) {
            // Blocks calling thread as long as this Server instance
            // is in activated state
            if (this.isActive()) {
                try {
                    // Wait until we are done
                    this.wait();
                } catch (final InterruptedException ex) {
                    // Server has been deactivated, return from run
                }
            }
        }
    }

    @Override
    public String getKind() {
        return "local-server";
    }

    @Override
    public Class<?> getDataType() {
        return null;
    }

};
