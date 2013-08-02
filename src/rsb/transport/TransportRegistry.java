package rsb.transport;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A registry of all know transports.
 *
 * @author jwienke
 */
public class TransportRegistry {

    private static final TransportRegistry DEFAULT_INSTANCE =
            new TransportRegistry();

    /**
     * A map from transport name to factory instances representing the available
     * transports.
     */
    private final Map<String, TransportFactory> factories =
            new HashMap<String, TransportFactory>();

    /**
     * Returns a default instance of this class with the globally available
     * transports.
     *
     * @return default instance
     */
    public static TransportRegistry getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    /**
     * Indicates whether a transport with the given name is available.
     *
     * @param name
     *            the name of the transport
     * @return <code>true</code> if a transport with the name exists, else
     *         <code>false</code>
     */
    public boolean hasTransport(final String name) {
        synchronized (this.factories) {
            return this.factories.containsKey(name);
        }
    }

    /**
     * Returns the transport factory for a transport with the specified name.
     *
     * @param name
     *            name of the transport
     * @return the transport factory instance
     * @throws NoSuchElementException
     *             there is no transport with the given name
     */
    public TransportFactory getFactory(final String name) {
        synchronized (this.factories) {
            if (!this.factories.containsKey(name)) {
                throw new NoSuchElementException("No transport with name '"
                        + name + "' available.");
            }
            return this.factories.get(name);
        }
    }

    /**
     * Registers a transport factory for a transport with the given name.
     *
     * @param name
     *            name of the transport
     * @param factory
     *            factory instance
     * @throws IllegalArgumentException
     *             There is already a transport with the given name or the name
     *             is <code>null</code> or empty.
     */
    public void registerTransport(final String name,
            final TransportFactory factory) {

        synchronized (this.factories) {

            if (this.factories.containsKey(name)) {
                throw new IllegalArgumentException(
                        "There is already a transport with name '" + name
                                + "'.");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException(
                        "Transport name must not be null or empty.");
            }
            if (factory == null) {
                throw new IllegalArgumentException(
                        "Factory instance must not be null.");
            }

            this.factories.put(name, factory);

        }

    }

    /**
     * Returns the names of all known transports.
     *
     * @return set of names
     */
    public Set<String> transportNames() {
        return this.factories.keySet();
    }

}
