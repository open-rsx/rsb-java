/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
package rsb.transport.socket;

import java.net.URI;
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
import rsb.converter.ConversionException;
import rsb.converter.ConverterSelectionStrategy;
import rsb.filter.Filter;
import rsb.filter.FilterAction;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.ProtocolConversion;
import rsb.transport.AbstractConnector;
import rsb.transport.EventHandler;
import rsb.transport.InConnector;
import rsb.transport.socket.Bus.NotificationReceiver;

/**
 * An {@link InConnector} for the socket-based transport.
 *
 * @author jwienke
 */
public class SocketInConnector extends AbstractConnector
                                   implements InConnector,
                                              NotificationReceiver {

    private static final Logger LOG = Logger
            .getLogger(SocketInConnector.class.getName());

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
    public SocketInConnector(final SocketOptions socketOptions,
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
        synchronized (this.utility) {
            this.utility.getBus().removeNotificationReceiver(this);
            this.utility.deactivate();
        }
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

    // we need to shield against user code terminating the framework code
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
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
                            ByteBuffer.wrap(notification.getData()
                                    .toByteArray()), this.utility
                                    .getConverters());

            // make a copy to avoid lengthy locking
            final Set<EventHandler> handlers =
                    new HashSet<EventHandler>(this.handlers);
            for (final EventHandler handler : handlers) {
                handler.handle(resultEvent);
            }

        } catch (final RuntimeException e) {
            LOG.log(Level.WARNING, "Error while dispatching notification to "
                    + "registered handlers. Ignoring this.", e);
        } catch (final ConversionException e) {
            LOG.log(Level.WARNING,
                    "Error decoding the received message. Ignroing this.", e);
        }

    }

    @Override
    public void notify(final Filter filter, final FilterAction action) {
        // transport level filtering is currently not supported
    }

    @Override
    public URI getTransportUri() {
        return this.utility.getBus().getTransportUri();
    }

}
