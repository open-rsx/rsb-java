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

import java.nio.ByteBuffer;

import rsb.Event;
import rsb.QualityOfServiceSpec;
import rsb.RSBException;
import rsb.Scope;
import rsb.converter.ConverterSelectionStrategy;
import rsb.converter.WireContents;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.protocol.ProtocolConversion;
import rsb.transport.OutConnector;
import rsb.util.ByteHelpers;

/**
 * An {@link OutConnector} instance for the socket transport.
 *
 * @author jwienke
 */
public class SocketOutConnector implements OutConnector {

    private final SocketConnectorUtility utility;

    /**
     * Constructor.
     *
     * @param socketOptions
     *            options for the socket
     * @param serverMode
     *            server mode to use
     * @param converters
     *            converters to use for serialization
     */
    public SocketOutConnector(final SocketOptions socketOptions,
            final ServerMode serverMode,
            final ConverterSelectionStrategy<ByteBuffer> converters) {
        this.utility =
                new SocketConnectorUtility(socketOptions, serverMode,
                        converters);
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
        this.utility.activate();
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
    public void push(final Event event) throws RSBException {

        event.getMetaData().setSendTime(0);
        final WireContents<ByteBuffer> data =
                ProtocolConversion.serializeEventData(event,
                        this.utility.getConverters());

        final Builder builder = Notification.newBuilder();
        builder.setEventId(ProtocolConversion.createEventIdBuilder(event
                .getId()));

        builder.setData(ByteHelpers.buteBufferToByteString(data
                .getSerialization()));

        ProtocolConversion.fillNotificationHeader(builder, event,
                data.getWireSchema());

        final Notification notification = builder.build();
        this.utility.getBus().handleOutgoing(notification);

    }

}
