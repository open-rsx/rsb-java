package rsb.transport.socket;

import java.nio.ByteBuffer;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.NoSuchConverterException;
import rsb.converter.WireContents;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.protocol.ProtocolConversion;
import rsb.transport.OutConnector;

import com.google.protobuf.ByteString;

/**
 * An {@link OutConnector} instance for the socket transport.
 *
 * @author jwienke
 */
public class SocketOutConnector implements OutConnector {

    private final SocketOptions socketOptions;
    private final ServerMode serverMode;
    private Bus bus;
    final ConverterSelectionStrategy<ByteBuffer> converters;

    public SocketOutConnector(final SocketOptions socketOptions,
            final ServerMode serverMode,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        assert socketOptions != null;
        assert converters != null;
        this.socketOptions = socketOptions;
        this.serverMode = serverMode;
        this.converters = converters;
    }

    @Override
    public String getType() {
        return "Socket";
    }

    @Override
    public void setQualityOfServiceSpec(final QualityOfServiceSpec spec) {
        // we always have reliable and ordered communication. No need to do
        // anything
    }

    @Override
    public void setScope(final Scope scope) {
        // nothing to do here. We don't need a scope
    }

    @Override
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

    @Override
    public void deactivate() throws RSBException {

        synchronized (this) {

            if (!isActive()) {
                throw new IllegalStateException("Not active");
            }

            this.bus.deactivate();
            this.bus = null;

        }

    }

    @Override
    public boolean isActive() {
        synchronized (this) {
            return this.bus != null;
        }
    }

    // TODO duplicated from SpreadOutConnector
    private WireContents<ByteBuffer> convertEvent(final Event event)
            throws ConversionException {
        try {
            final Converter<ByteBuffer> converter = this.converters
                    .getConverter(event.getType().getName());
            final WireContents<ByteBuffer> convertedDataBuffer = converter
                    .serialize(event.getType(), event.getData());
            return convertedDataBuffer;
        } catch (final NoSuchConverterException e) {
            throw new ConversionException(e);
        }
    }

    // TODO mostly duplicates spread connector
    @Override
    public void push(final Event event) throws RSBException {

        event.getMetaData().setSendTime(0);
        final WireContents<ByteBuffer> data = convertEvent(event);

        final Builder builder = Notification.newBuilder();
        builder.setEventId(ProtocolConversion.createEventIdBuilder(event
                .getId()));

        builder.setData(ByteString.copyFrom(data.getSerialization().array(), 0,
                data.getSerialization().limit()));

        ProtocolConversion.fillNotificationHeader(builder, event,
                data.getWireSchema());

        final Notification notification = builder.build();
        System.out.println("Sent notification: " + notification.toString());
        this.bus.handleOutgoing(notification);

    }
}
