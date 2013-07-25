package rsb.transport.socket;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConverterSelectionStrategy;
import rsb.filter.AbstractFilterObserver;
import rsb.protocol.NotificationType.Notification;
import rsb.transport.EventBuilder;
import rsb.transport.EventHandler;
import rsb.transport.InPushConnector;
import rsb.transport.socket.Bus.NotificationReceiver;

/**
 * An {@link InPushConnector} for the socket-based transport.
 *
 * @author jwienke
 */
public class SocketInPushConnector extends AbstractFilterObserver implements
        InPushConnector, NotificationReceiver {

    private static final Logger LOG = Logger
            .getLogger(SocketInPushConnector.class.getName());

    private Scope scope;
    private final SocketOptions socketOptions;
    private final ServerMode serverMode;
    private final ConverterSelectionStrategy<ByteBuffer> converters;
    private Bus bus;
    private final Set<EventHandler> handlers = Collections
            .synchronizedSet(new HashSet<EventHandler>());

    public SocketInPushConnector(final SocketOptions socketOptions,
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
        // we don't have to do anything here as we are always ordered and
        // reliable
    }

    @Override
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    @Override
    public void activate() throws RSBException {

        // TODO largely duplicated
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

            // XXX this is not duplicated
            this.bus.addNotificationReceiver(this);

        }

    }

    @Override
    public void deactivate() throws RSBException {

        // TODO complete duplication
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

    @Override
    public void addHandler(final EventHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public boolean removeHandler(final EventHandler handler) {
        return this.handlers.remove(handler);
    }

    @Override
    public void handle(final Notification notification) {

        LOG.log(Level.FINEST, "Received a notification with scope {0}",
                notification.getScope().toStringUtf8());
        final Scope notificationScope = new Scope(notification.getScope()
                .toStringUtf8());
        if (!this.scope.equals(notificationScope)
                && !notificationScope.isSubScopeOf(this.scope)) {
            LOG.log(Level.FINER,
                    "Ignoring notification on scope {0} because it is not a (sub)scope of {1}",
                    new Object[] { notificationScope, this.scope });
            return;
        }

        try {

            final Event resultEvent = EventBuilder.fromNotification(
                    notification,
                    notification.getData().asReadOnlyByteBuffer(),
                    this.converters);

            // make a copy to avoid lengthy locking
            final Set<EventHandler> handlers = new HashSet<EventHandler>(
                    this.handlers);
            for (final EventHandler handler : handlers) {
                handler.handle(resultEvent);
            }

        } catch (final Exception e) {
            // TODO here we would have to use an error handler
            LOG.log(Level.WARNING,
                    "Error while dispatching notification to registered handlers. Ignoring this.",
                    e);
        }

    }

}
