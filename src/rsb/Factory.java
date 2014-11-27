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

import rsb.config.ParticipantConfig;
import rsb.config.ParticipantConfigCreator;
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
 * @author swrede
 */
public final class Factory {

    /**
     * The singleton instance.
     */
    private static Factory instance = new Factory();

    private final Properties properties = new Properties();
    private final ParticipantConfig defaultConfig = new ParticipantConfig();

    /**
     * Private constructor to ensure Singleton.
     */
    private Factory() {

        DefaultConverters.register();
        DefaultTransports.register();

        // construct default participant config with default transport
        this.defaultConfig.getOrCreateTransport("socket").setEnabled(true);

        // handle configuration
        new ConfigLoader().load(this.properties);
        new ParticipantConfigCreator().reconfigure(this.defaultConfig,
                this.properties);

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
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final Scope scope,
            final Class<?> type) throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(scope).setType(type));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final String scope,
            final Class<?> type) throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(new Scope(scope)).setType(type));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @param config
     *            participant config to use
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final Scope scope,
            final Class<?> type, final ParticipantConfig config)
            throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(scope).setType(type).setConfig(config));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param type
     *            type identifier of the informer
     * @param config
     *            participant config to use
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final String scope,
            final Class<?> type, final ParticipantConfig config)
            throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(new Scope(scope)).setType(type).setConfig(config));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final Scope scope)
            throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(scope));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final String scope)
            throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(new Scope(scope)));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param config
     *            participant config to use
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final Scope scope,
            final ParticipantConfig config) throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(scope).setConfig(config));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param scope
     *            scope of the informer
     * @param config
     *            participant config to use
     * @return new informer instance
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final String scope,
            final ParticipantConfig config) throws InitializeException {
        return this.createInformer(new InformerCreateArgs().setScope(new Scope(scope)).setConfig(config));
    }

    /**
     * Creates a new informer instance.
     *
     * @param <DataType>
     *            type of the data sent by this informer
     * @param args
     *            Parameter object with create arguments for participant.
     * @return new informer
     * @throws InitializeException
     *             error initializing the informer
     */
    public <DataType> Informer<DataType> createInformer(final InformerCreateArgs args)
            throws InitializeException {
        return new Informer<DataType>(args.getScope(), args.getType(), (args.getConfig()==null) ? this.defaultConfig : args.getConfig());
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
        return createListener(new ListenerCreateArgs().setScope(scope));
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
        return createListener(new ListenerCreateArgs().setScope(new Scope(scope)));
    }

    /**
     * Creates a new listener instance.
     *
     * @param scope
     *            scope of the listener
     * @param config
     *            participant configuration to use
     * @return new listener
     * @throws InitializeException
     *             error initializing the listener
     */
    public Listener createListener(final String scope,
            final ParticipantConfig config) throws InitializeException {
        return createListener(new ListenerCreateArgs().setScope(new Scope(scope)).setConfig(config));
    }

    /**
     * Creates a new listener instance.
     *
     * @param scope
     *            scope of the listener
     * @param config
     *            participant configuration to use
     * @return new listener
     * @throws InitializeException
     *             error initializing the listener
     */
    public Listener createListener(final Scope scope,
            final ParticipantConfig config) throws InitializeException {
        return createListener(new ListenerCreateArgs().setScope(scope).setConfig(config));
    }

    /**
     * Creates a new listener instance.
     *
     * @param args
     *            Parameter object with create arguments for participant.
     * @return new listener
     * @throws InitializeException
     *             error initializing the listener
     */
    public Listener createListener(final ListenerCreateArgs args)
            throws InitializeException {
        return new Listener(args.getScope(), (args.getConfig()==null) ? this.defaultConfig : args.getConfig());
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
        return createLocalServer(new LocalServerCreateArgs().setScope(scope));
    }

    /**
     * Creates a new LocalServer object which exposes methods under the scope @a
     * scope.
     *
     * @param scope
     *            The scope under which methods of the LocalServer object should
     *            be exposed.
     * @param config
     *            participant configuration to use
     * @return The new LocalServer object.
     */
    public LocalServer createLocalServer(final Scope scope,
            final ParticipantConfig config) {
        return createLocalServer(new LocalServerCreateArgs().setScope(scope).setConfig(config));
    }

    /**
     * Creates a new LocalServer object which exposes methods under the scope @a
     * scope.
     *
     * @param scope
     *            The scope under which methods of the LocalServer object should
     *            be exposed.
     * @param config
     *            participant configuration to use
     * @return The new LocalServer object.
     */
    public LocalServer createLocalServer(final String scope,
            final ParticipantConfig config) {
        return createLocalServer(new LocalServerCreateArgs().setScope(new Scope(scope)).setConfig(config));
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
        return createLocalServer(new LocalServerCreateArgs().setScope(new Scope(scope)));
    }

    /**
     * Creates a new LocalServer instance.
     *
     * @param args
     *         Parameter object with create arguments for participant.
     * @return new LocalServer
     */
    public LocalServer createLocalServer(final LocalServerCreateArgs args) {
        return new LocalServer(args.getScope(), (args.getConfig()==null) ? this.defaultConfig : args.getConfig());
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
        return createRemoteServer(new RemoteServerCreateArgs().setScope(scope));
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
        return createRemoteServer(new RemoteServerCreateArgs().setScope(new Scope(scope)));
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @param config
     *            participant configuration to use
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final Scope scope,
            final ParticipantConfig config) {
        return createRemoteServer(new RemoteServerCreateArgs().setScope(scope).setConfig(config));
    }

    /**
     * Creates a new RemoteServer object which is suitable for calling methods
     * provided by a remote server under the scope @a scope.
     *
     * @param scope
     *            The scope under which a remote server provides its methods.
     * @param config
     *            participant configuration to use
     * @return The new RemoteServer object.
     */
    public RemoteServer createRemoteServer(final String scope,
            final ParticipantConfig config) {
        return createRemoteServer(new RemoteServerCreateArgs().setScope(new Scope(scope)).setConfig(config));
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
        return createRemoteServer(new RemoteServerCreateArgs().setScope(scope).setTimeout(timeout));
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
        return createRemoteServer(new RemoteServerCreateArgs().setScope(new Scope(scope)).setTimeout(timeout));
    }

    /**
     * Creates a new remote server instance.
     *
     * @param args
     *         Parameter object with create arguments for participant.
     * @return new remote server
     */
    public RemoteServer createRemoteServer(final RemoteServerCreateArgs args) {
        if (args.getTimeout() != 0) {
            return new RemoteServer(args.getScope(), args.getTimeout(), (args.getConfig()==null) ? this.defaultConfig : args.getConfig());
        } else {
            return new RemoteServer(args.getScope(), (args.getConfig()==null) ? this.defaultConfig : args.getConfig());
        }
    }

    /**
     * Returns the default configuration properties.
     *
     * @return property instances
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Returns the participant config used per default for new participants.
     *
     * This instance might be modified but changes are only applied to newly
     * created participants.
     *
     * @return default participant config
     */
    public ParticipantConfig getDefaulParticipantConfig() {
        return this.defaultConfig;
    }

}
