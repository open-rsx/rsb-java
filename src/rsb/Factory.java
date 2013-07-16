/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2010 CoR-Lab, Bielefeld University
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

package rsb;

import rsb.converter.DefaultConverters;
import rsb.patterns.LocalServer;
import rsb.patterns.RemoteServer;
import rsb.transport.DefaultTransports;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

/**
 * A factory for RSB client-level objects. This class is a Singleton.
 *
 * @author jwienke
 */
public final class Factory {

    /**
     * The singleton instance.
     */
    private static Factory instance = new Factory();

    private final Properties properties = new Properties();

    /**
     * Private constructor to ensure Singleton.
     */
    private Factory() {
        DefaultConverters.register();
        DefaultTransports.register();
        new ConfigLoader().load(this.properties);
    }

    /**
     * Returns the one and only instance of this class.
     *
     * @return singleton factory instance
     */
    public static Factory getInstance() {
        return instance;
    }

    /**
     * Creates a new informer instance.
     *
     * @param <T>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <T> Informer<T> createInformer(final Scope scope, final Class<?> type)
            throws InitializeException {
        return new Informer<T>(scope, type, this.properties);
    }

    /**
     * Creates a new informer instance.
     *
     * @param <T>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <T> Informer<T> createInformer(final String scope,
            final Class<?> type) throws InitializeException {
        return new Informer<T>(new Scope(scope), type, this.properties);
    }

    /**
     * Creates a new informer instance.
     *
     * @param <T>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <T> Informer<T> createInformer(final Scope scope)
            throws InitializeException {
        return new Informer<T>(scope, this.properties);
    }

    /**
     * Creates a new informer instance.
     *
     * @param <T>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <T> Informer<T> createInformer(final String scope)
            throws InitializeException {
        return new Informer<T>(new Scope(scope), this.properties);
    }

    /**
     * Creates a new listener instance.
     *
     * @param scope
     *            scope of the listener
     * @return new listener
     * @throws InitializeException
     *             error initializing the listener
     */
    public Listener createListener(final Scope scope)
            throws InitializeException {
        return new Listener(scope, this.properties);
    }

    /**
     * Creates a new listener instance.
     *
     * @param scope
     *            scope of the listener
     * @return new listener
     * @throws InitializeException
     *             error initializing the listener
     */
    public Listener createListener(final String scope)
            throws InitializeException {
        return new Listener(scope, this.properties);
    }

    /**
     * Creates a new LocalServer object which exposes methods under the scope @a
     * scope.
     *
     * @param scope
     *            The scope under which methods of the LocalServer object should
     *            be exposed.
     * @return The new LocalServer object.
     */
    public LocalServer createLocalServer(final Scope scope) {
        return new LocalServer(scope);
    }

    /**
     * Creates a new LocalServer object which exposes methods under the scope @a
     * scope.
     *
     * @param scope
     *            The scope under which methods of the LocalServer object should
     *            be exposed.
     * @return The new LocalServer object.
     */
    public LocalServer createLocalServer(final String scope) {
        return new LocalServer(scope);
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final Scope scope) {
        return new RemoteServer(scope);
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final String scope) {
        return new RemoteServer(scope);
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @param timeout
     *            The amount of seconds methods calls should wait for their
     *            replies to arrive before failing.
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final Scope scope,
            final double timeout) {
        return new RemoteServer(scope, timeout);
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @param timeout
     *            The amount of seconds methods calls should wait for their
     *            replies to arrive before failing.
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final String scope,
            final double timeout) {
        return new RemoteServer(scope, timeout);
    }

    /**
     * Returns the default configuration properties.
     *
     * @return property instances
     */
    public Properties getProperties() {
        return this.properties;
    }

}
