package rsb.transport.socket;

import java.nio.ByteBuffer;

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

            final Bus bus =
                    new RefCountingBus(creator.create(options),
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
        return getBusFromCache(options, busServerCache, new BusCreator() {

            @Override
            public Bus create(final SocketOptions options) {
                return new BusServer(options);
            }

        });
    }

    private static Bus getBusClient(final SocketOptions options)
            throws RSBException {
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
