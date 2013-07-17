/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2012 CoR-Lab, Bielefeld University
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rsb.Event;
import rsb.EventId;
import rsb.ParticipantId;
import rsb.Scope;
import rsb.converter.ConversionException;
import rsb.converter.Converter;
import rsb.converter.Uint64Converter;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.NotificationType.Notification.Builder;
import rsb.protocol.ProtocolConversion;

import com.google.protobuf.ByteString;

/**
 * @author jwienke
 * @author swrede
 */
public class BusConnectionRoundtripTest {

    private ServerSocket serverSocket;
    private BusClientConnection client;
    private BusServerConnection server;

    @After
    public void tearDown() throws Throwable {
        try {
            if (this.client != null) {
                this.client.deactivate();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
        try {
            if (this.server != null) {
                this.server.deactivate();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (final Exception e) {
            // we cannot do anything here
        }
    }

    @Before
    public void mutualConnection() throws Throwable {

        final int port = 55555;

        this.serverSocket = new ServerSocket(port);

        this.client = new BusClientConnection(InetAddress.getLocalHost(), port);
        assertFalse(this.client.isActive());
        this.client.activate();

        this.server = new BusServerConnection(this.serverSocket.accept());
        assertFalse(this.server.isActive());
        this.server.activate();
        this.server.handshake();

        this.client.handshake();

        assertTrue(this.client.isActive());
        assertTrue(this.server.isActive());

    }

    @Test
    public void clientToServer() throws Throwable {

        final Notification sent = createNotification();
        this.client.sendNotification(sent);

        final Notification received = this.server.readNotification();

        assertEquals(sent, received);

    }

    @Test
    public void serverToClient() throws Throwable {

        final Notification sent = createNotification();
        this.server.sendNotification(sent);

        final Notification received = this.client.readNotification();

        assertEquals(sent, received);

    }

    private Notification createNotification() throws ConversionException {

        // create a dummy event to send around
        final Event event = new Event(Long.class, new Long(42));
        event.setId(new EventId(new ParticipantId(), 7));
        event.setScope(new Scope("/test/"));
        final Builder builder = Notification.newBuilder();
        builder.setEventId(ProtocolConversion.createEventIdBuilder(event
                .getId()));
        final Converter<ByteBuffer> converter = new Uint64Converter();
        builder.setData(ByteString.copyFrom(converter
                .serialize(event.getType(), event.getData()).getSerialization()
                .array()));

        ProtocolConversion.fillNotificationHeader(builder, event, converter
                .getSignature().getSchema());

        return builder.build();

    }

}
