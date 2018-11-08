/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
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
package rsb.transport.socket;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.RSBException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.socket.RefCountingBus.DeactivationHandler;

/**
 * A class providing base functionality for {@link rsb.transport.Connector}
 * implementations of the socket transport. It is intended to be used via
 * composition.
 *
 * Provided methods are generally thread-safe if not indicated otherwise. The
 * respective instance of this class is used for synchronizing in case clients
 * of this class need to extend synchronized blocks or interact with the
 * synchronization strategies of this class.
 *
 * @author jwienke
 */
public class SocketConnectorUtility {

    private static final Logger LOG = Logger.getLogger(
            SocketConnectorUtility.class.getName());

    private static BusCache busClientCache = new BusCache();
    private static BusCache busServerCache = new BusCache();

    private final SocketOptions socketOptions;
    private final ServerMode serverMode;
    private final ConverterSelectionStrategy<ByteBuffer> converters;
    private Bus bus;

    /**
     * Constructor.
     *
     * @param socketOptions
     *            socket options to use
     * @param serverMode
     *            the kind of server mode to use
     * @param converters
     *            the converters to use for serialization
     */
    public SocketConnectorUtility(final SocketOptions socketOptions,
            final ServerMode serverMode,
            final ConverterSelectionStrategy<ByteBuffer> converters) {

        assert socketOptions != null;
        assert converters != null;

        this.socketOptions = socketOptions;
        this.serverMode = serverMode;
        this.converters = converters;

    }

    /**
     * Returns the underlying bus instance when called after {@link #activate()}
     * .
     *
     * @return bus instance or <code>null</code>
     */
    public Bus getBus() {
        return this.bus;
    }

    /**
     * Returns the contained converters to use.
     *
     * @return converters
     */
    public ConverterSelectionStrategy<ByteBuffer> getConverters() {
        return this.converters;
    }

    /**
     * Implementations of this interface provide a factory method for creating
     * {@link Bus} instances.
     *
     * @author jwienke
     */
    private interface BusCreator {

        /**
         * Creates a new instance.
         *
         * @param options
         *            socket options for that instance
         * @return new instance, not <code>null</code>
         */
        Bus create(SocketOptions options);

    }

    private static Bus getBusFromCache(final SocketOptions options,
            final BusCache cache, final BusCreator creator) throws RSBException {

        synchronized (cache.getSynchronizer()) {

            if (cache.hasBus(options)) {
                final Bus bus = cache.get(options);
                bus.activate();
                return bus;
            }

            final Bus bus = new RefCountingBus(creator.create(options),
                    new DeactivationHandler() {

                        @Override
                        public void
                                deactivated(final RefCountingBus bus) {
                            synchronized (cache.getSynchronizer()) {
                                cache.unregister(bus);
                            }
                        }

                    });
            bus.activate();
            cache.register(bus);
            return bus;

        }

    }

    private static Bus getBusServer(final SocketOptions options)
            throws RSBException {
        LOG.log(Level.FINE, "Acquiring new bus server with options {0}",
                options);
        return getBusFromCache(options, busServerCache, new BusCreator() {

            @Override
            public Bus create(final SocketOptions options) {
                return new BusServer(options);
            }

        });
    }

    private static Bus getBusClient(final SocketOptions options)
            throws RSBException {
        LOG.log(Level.FINE, "Acquiring new bus client with options {0}",
                options);
        return getBusFromCache(options, busClientCache, new BusCreator() {

            @Override
            public Bus create(final SocketOptions options) {
                return new BusClient(options);
            }

        });
    }

    /**
     * Acquires a new {@link Bus} instance and activates it respecting the
     * requested {@link ServerMode}.
     *
     * @throws RSBException
     *             error initializing {@link Bus}
     */
    public void activate() throws RSBException {
        LOG.log(Level.FINE, "Starting activation with serverMode={0}",
                this.serverMode);

        synchronized (this) {

            if (isActive()) {
                throw new IllegalStateException("Already active");
            }

            switch (this.serverMode) {
            case YES:
                this.bus = getBusServer(this.socketOptions);
                break;
            case NO:
                this.bus = getBusClient(this.socketOptions);
                break;
            case AUTO:
                try {
                    this.bus = getBusServer(this.socketOptions);
                } catch (final RSBException e) {
                    LOG.log(Level.FINE, "Acquiring bus server failed", e);
                    this.bus = getBusClient(this.socketOptions);
                }
                break;
            default:
                assert false;
                throw new RSBException("Unexpected server mode requested: "
                        + this.serverMode);
            }

        }

    }

    /**
     * Deactivates the underlying bus instance.
     *
     * @throws RSBException
     *             error deactivating {@link Bus}
     * @throws InterruptedException
     *             interrupted while waiting for the bus to terminate
     */
    public void deactivate() throws RSBException, InterruptedException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Not active");
            }

            this.bus.deactivate();
            this.bus = null;

        }

    }

    /**
     * Indicates whether this class has been activated or not.
     *
     * @return <code>true</code> if activated
     */
    public boolean isActive() {
        synchronized (this) {
            return this.bus != null;
        }
    }

}
