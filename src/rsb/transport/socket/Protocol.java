package rsb.transport.socket;

/**
 * Class with helpers and constants describing the socket-based protocol.
 *
 * @author jwienke
 */
public final class Protocol {

    private Protocol() {
        super();
        // prevent instantiation of utility class
    }

    /**
     * The data to send for handshake requests and replies
     */
    public static final int HANDSHAKE_DATA = 0x00000000;

    /**
     * The number of bytes to send and expect for handshake messages.
     */
    public static final int HANDSHAKE_BYTES = 4;

    /**
     * The number of bytes encoding the data size to be sent.
     */
    public static final int DATA_SIZE_BYTES = 4;

}
