package rsb.transport.socket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache for {@link Bus} instances based on the required {@link SocketOptions}
 * .
 *
 * This class is thread-safe. For longer synchronized blocks use
 * {@link #getSynchronizer()}.
 *
 * @author jwienke
 */
public class BusCache {

    private final Map<SocketOptions, Bus> cache = Collections
            .synchronizedMap(new HashMap<SocketOptions, Bus>());

    /**
     * Clients may use the returned instance to synchronize on.
     *
     * @return object to synchronize on
     */
    public Object getSynchronizer() {
        return this.cache;
    }

    /**
     * Registers a new bus instance in the cache.
     *
     * @param bus
     *            bus to register
     * @throws IllegalArgumentException
     *             there is already a bus with the given options
     */
    public void register(final Bus bus) {
        register(bus, false);
    }

    /**
     * Registers a new bus instance in the cache and allows overriding existing
     * instances.
     *
     * @param bus
     *            bus to registers
     * @param replace
     *            if <code>true</code>, replace existing instances with the same
     *            options instead of throwing an exception
     * @throws IllegalArgumentException
     *             there is already a bus with the given options and replacing
     *             was not requested
     */
    public void register(final Bus bus, final boolean replace) {
        synchronized (this.cache) {
            if (!replace && this.cache.containsKey(bus.getSocketOptions())) {
                throw new IllegalArgumentException(
                        "There is already a cached bus for options "
                                + bus.getSocketOptions());
            }
            this.cache.put(bus.getSocketOptions(), bus);
        }
    }

    /**
     * Returns a {@link Bus} instance for the given options or <code>null</code>
     * if no such instance is cached.
     *
     * @param options
     *            options to look up
     * @return {@link Bus} instance or <code>null</code>
     */
    public Bus get(final SocketOptions options) {
        return this.cache.get(options);
    }

    /**
     * Indicates whether a {@link Bus} instance with the given options is
     * available or not.
     *
     * @param options
     *            options to look up
     * @return <code>true</code> if available, else <code>false</code>
     */
    public boolean hasBus(final SocketOptions options) {
        return this.cache.containsKey(options);
    }

}
