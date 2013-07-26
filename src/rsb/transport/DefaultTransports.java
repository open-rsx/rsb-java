package rsb.transport;

import rsb.transport.socket.SocketFactory;
import rsb.transport.spread.SpreadFactory;

/**
 * A class statically registering all directly implemented transports.
 *
 * @author jwienke
 */
public final class DefaultTransports {

    private static Boolean registered = false;

    private DefaultTransports() {
        super();
        // prevent instantiation of a utility class
    }

    /**
     * Registers the known transports. Can be called multiple times.
     */
    public static void register() {

        synchronized (registered) {

            if (registered) {
                return;
            }

            TransportRegistry.getDefaultInstance().registerTransport("spread",
                    new SpreadFactory());
            TransportRegistry.getDefaultInstance().registerTransport("socket",
                    new SocketFactory());

            registered = true;

        }

    }

}
