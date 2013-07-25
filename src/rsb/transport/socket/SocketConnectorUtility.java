package rsb.transport.socket;

import java.nio.ByteBuffer;

import rsb.RSBException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.transport.Connector;

/**
 * A class providing base functionality for {@link Connector} implementations of
 * the socket transport. It is intended to be used via composition.
 *
 * Provided methods are generally thread-safe if not indicated otherwise. The
 * respective instance of this class is used for synchronizing in case clients
 * of this class need to extend synchronized blocks or interact with the
 * synchronization strategies of this class.
 *
 * @author jwienke
 */
public class SocketConnectorUtility {

    private final SocketOptions socketOptions;
    private final ServerMode serverMode;
    private final ConverterSelectionStrategy<ByteBuffer> converters;
    private Bus bus;

    public SocketConnectorUtility(final SocketOptions socketOptions,
            final ServerMode serverMode,
            final ConverterSelectionStrategy<ByteBuffer> converters) {

        assert socketOptions != null;
        assert converters != null;

        this.socketOptions = socketOptions;
        this.serverMode = serverMode;
        this.converters = converters;

    }

    public Bus getBus() {
        return this.bus;
    }

    public ConverterSelectionStrategy<ByteBuffer> getConverters() {
        return this.converters;
    }

    public void activate() throws RSBException {

        synchronized (this) {

            if (isActive()) {
                throw new IllegalStateException("Already active");
            }

            switch (this.serverMode) {
            case YES:
                this.bus = new BusServer(this.socketOptions);
                this.bus.activate();
                break;
            case NO:
                this.bus = new BusClient(this.socketOptions);
            case AUTO:
                try {
                    this.bus = new BusServer(this.socketOptions);
                    this.bus.activate();
                } catch (final RSBException e) {
                    this.bus = new BusClient(this.socketOptions);
                    this.bus.activate();
                }
                break;
            default:
                assert false;
                throw new RSBException("Unexpected server mode requested: "
                        + this.serverMode);
            }

        }

    }

    public void deactivate() throws RSBException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Not active");
            }

            this.bus.deactivate();
            this.bus = null;

        }

    }

    public boolean isActive() {
        synchronized (this) {
            return this.bus != null;
        }
    }

}
