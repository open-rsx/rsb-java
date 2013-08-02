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
import rsb.protocol.ProtocolConversion;
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

    private final SocketConnectorUtility utility;
    private Scope scope;
    private final Set<EventHandler> handlers = Collections
            .synchronizedSet(new HashSet<EventHandler>());

    /**
     * Constructor.
     *
     * @param socketOptions
     *            socket options to use
     * @param serverMode
     *            server mode to use
     * @param converters
     *            converters to use for serialization
     */
    public SocketInPushConnector(final SocketOptions socketOptions,
            final ServerMode serverMode,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        this.utility =
                new SocketConnectorUtility(socketOptions, serverMode,
                        converters);
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
        synchronized (this.utility) {
            this.utility.activate();
            this.utility.getBus().addNotificationReceiver(this);
        }
    }

    @Override
    public void deactivate() throws RSBException, InterruptedException {
        this.utility.deactivate();
    }

    @Override
    public boolean isActive() {
        return this.utility.isActive();
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
        final Scope notificationScope =
                new Scope(notification.getScope().toStringUtf8());
        if (!this.scope.equals(notificationScope)
                && !notificationScope.isSubScopeOf(this.scope)) {
            LOG.log(Level.FINER, "Ignoring notification on scope {0} "
                    + "because it is not a (sub)scope of {1}", new Object[] {
                    notificationScope, this.scope });
            return;
        }

        try {

            final Event resultEvent =
                    ProtocolConversion.fromNotification(notification,
                            notification.getData().asReadOnlyByteBuffer(),
                            this.utility.getConverters());

            // make a copy to avoid lengthy locking
            final Set<EventHandler> handlers =
                    new HashSet<EventHandler>(this.handlers);
            for (final EventHandler handler : handlers) {
                handler.handle(resultEvent);
            }

        } catch (final Exception e) {
            // TODO here we would have to use an error handler
            LOG.log(Level.WARNING, "Error while dispatching notification to "
                    + "registered handlers. Ignoring this.", e);
        }

    }

}
