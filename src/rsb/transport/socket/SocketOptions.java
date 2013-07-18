package rsb.transport.socket;

import java.net.InetAddress;

/**
 * A class representing the different options of socket-based communications.
 *
 * @author jwienke
 */
public final class SocketOptions {

    private final InetAddress address;
    private final int port;
    private final boolean tcpNoDelay;

    public SocketOptions(final InetAddress address, final int port,
            final boolean tcpNoDelay) {
        assert address != null;
        assert port >= 0;

        this.address = address;
        this.port = port;
        this.tcpNoDelay = tcpNoDelay;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isTcpNoDelay() {
        return this.tcpNoDelay;
    }

    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof SocketOptions)) {
            return false;
        }

        final SocketOptions other = (SocketOptions) obj;

        return this.address.equals(other.address) && this.port == other.port
                && this.tcpNoDelay == other.tcpNoDelay;

    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + this.address.hashCode();
        result = prime * result + this.port;
        result = prime * result + Boolean.valueOf(this.tcpNoDelay).hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName());
        builder.append("[address=");
        builder.append(this.address);
        builder.append(", port=");
        builder.append(this.port);
        builder.append(", tcpNoDelay=");
        builder.append(this.tcpNoDelay);
        builder.append(']');
        return builder.toString();
    }

}
