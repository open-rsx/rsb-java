package rsb.transport.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import rsb.InitializeException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.DefaultConverterRepository;
import rsb.transport.InPushConnector;
import rsb.transport.OutConnector;
import rsb.transport.TransportFactory;
import rsb.util.Properties;

/**
 * A {@link TransportFactory} for the socket-based transport.
 *
 * @author jwienke
 */
public class SocketFactory implements TransportFactory {

    private static final String SERVER_MODE_NO = "0";
    private static final String SERVER_MODE_YES = "1";
    private static final String SERVER_MODE_AUTO = "auto";

    private static final String PORT_KEY = "transport.socket.port";
    private static final int DEFAULT_PORT = 55555;
    private static final String HOST_KEY = "transport.socket.host";
    private static final String DEFAULT_HOST = "localhost";
    private static final String NODELAY_KEY = "transport.socket.nodelay";
    private static final boolean DEFAULT_NODELAY = true;
    private static final String SERVER_MODE_KEY = "transport.socket.server";
    private static final String DEFAULT_SERVER_MODE = SERVER_MODE_AUTO;

    private SocketOptions parseSocketOptions(final Properties properties)
            throws InitializeException {

        try {

            final int port =
                    properties.getProperty(PORT_KEY, DEFAULT_PORT).asInteger();
            if (port < 0) {
                throw new InitializeException("Port must be a number >= 0");
            }
            final InetAddress address =
                    InetAddress.getByName(properties.getProperty(HOST_KEY,
                            DEFAULT_HOST).asString());
            final boolean tcpNoDelay =
                    properties.getProperty(NODELAY_KEY, DEFAULT_NODELAY)
                            .asBoolean();

            return new SocketOptions(address, port, tcpNoDelay);

        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve hostname", e);
        }

    }

    private ServerMode parseServerMode(final Properties properties)
            throws InitializeException {

        final String serverModeString =
                properties.getProperty(SERVER_MODE_KEY, DEFAULT_SERVER_MODE)
                        .asString();
        ServerMode serverMode;
        if (SERVER_MODE_AUTO.equals(serverModeString)) {
            serverMode = ServerMode.AUTO;
        } else if (SERVER_MODE_YES.equals(serverModeString)) {
            serverMode = ServerMode.YES;
        } else if (SERVER_MODE_NO.equals(serverModeString)) {
            serverMode = ServerMode.NO;
        } else {
            throw new InitializeException(String.format(
                    "Unsupported server mode '{0}'", serverModeString));
        }

        return serverMode;

    }

    @Override
    public OutConnector createOutConnector(final Properties properties)
            throws InitializeException {

        final ConverterSelectionStrategy<ByteBuffer> converters =
                DefaultConverterRepository.getDefaultConverterRepository()
                        .getConvertersForSerialization();

        return new SocketOutConnector(parseSocketOptions(properties),
                parseServerMode(properties), converters);

    }

    @Override
    public InPushConnector createInPushConnector(final Properties properties)
            throws InitializeException {

        final ConverterSelectionStrategy<ByteBuffer> converters =
                DefaultConverterRepository.getDefaultConverterRepository()
                        .getConvertersForDeserialization();

        return new SocketInPushConnector(parseSocketOptions(properties),
                parseServerMode(properties), converters);

    }

}
