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

    private SocketOptions parseSocketOptions(final Properties properties)
            throws InitializeException {

        try {

            final int port = properties.getProperty("trnaport.socket.port",
                    55555).asInteger();
            if (port < 0) {
                throw new InitializeException("Port must be a number >= 0");
            }
            final InetAddress address = InetAddress.getByName(properties
                    .getProperty("transport.socket.host", "localhost")
                    .asString());
            final boolean tcpNoDelay = properties.getProperty(
                    "transport.socket.nodelay", true).asBoolean();

            return new SocketOptions(address, port, tcpNoDelay);

        } catch (final UnknownHostException e) {
            throw new InitializeException("Unable to resolve hostname", e);
        }

    }

    private ServerMode parseServerMode(final Properties properties)
            throws InitializeException {

        final String serverModeString = properties.getProperty(
                "transport.socket.server", "auto").asString();
        ServerMode serverMode;
        if ("auto".equals(serverModeString)) {
            serverMode = ServerMode.AUTO;
        } else if ("1".equals(serverModeString)) {
            serverMode = ServerMode.YES;
        } else if ("0".equals(serverModeString)) {
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

        final ConverterSelectionStrategy<ByteBuffer> converters = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForSerialization();

        return new SocketOutConnector(parseSocketOptions(properties),
                parseServerMode(properties), converters);

    }

    @Override
    public InPushConnector createInPushConnector(final Properties properties)
            throws InitializeException {

        final ConverterSelectionStrategy<ByteBuffer> converters = DefaultConverterRepository
                .getDefaultConverterRepository()
                .getConvertersForDeserialization();

        return new SocketInPushConnector(parseSocketOptions(properties),
                parseServerMode(properties), converters);

    }

}
