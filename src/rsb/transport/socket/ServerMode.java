package rsb.transport.socket;

/**
 * An enum indicating which kind of server mode is requested by the user for the
 * socket transport.
 *
 * @author jwienke
 */
public enum ServerMode {

    /**
     * Always create a socket server. This may fail if a socket server with same
     * settings already exists on the host / network.
     */
    YES,

    /**
     * Never be a socket server. Instead use an existing server or fail if none
     * can be found.
     */
    NO,

    /**
     * Try to decide automatically whether a socket server is required and start
     * one if this is the case. Otherwise, reuse an existing one.
     */
    AUTO

}
