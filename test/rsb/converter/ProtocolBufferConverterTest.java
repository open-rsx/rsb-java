/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2011 CoR-Lab, Bielefeld University
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
package rsb.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import org.junit.Test;

import rsb.ParticipantId;
import rsb.protocol.EventIdType.EventId;
import rsb.protocol.NotificationType.Notification;

import com.google.protobuf.ByteString;

/**
 * @author swrede
 */
public class ProtocolBufferConverterTest {

    private WireContents<ByteBuffer> buffer;
    private final ProtocolBufferConverter<Notification> converter = new ProtocolBufferConverter<Notification>(
            Notification.getDefaultInstance());

    public void serialize() throws ConversionException {
        final Notification.Builder notificationBuilder = Notification
                .newBuilder();
        // notification metadata
        final EventId.Builder eventIdBuilder = EventId.newBuilder();
        eventIdBuilder.setSequenceNumber(23);
        eventIdBuilder.setSenderId(ByteString.copyFrom(new ParticipantId()
                .toByteArray()));
        notificationBuilder.setEventId(eventIdBuilder);
        notificationBuilder.setWireSchema(ByteString
                .copyFromUtf8("rsb.notification"));
        notificationBuilder.setScope(ByteString.copyFromUtf8("rsb"));

        this.buffer = this.converter.serialize(Notification.class,
                notificationBuilder.build());
        assertNotNull(this.buffer);
    }

    public void deserialize() throws ConversionException {
        assertNotNull(this.buffer);
        final UserData<Notification> result = this.converter.deserialize(
                ".rsb.protocol.Notification", this.buffer.getSerialization());
        final Notification notification = (Notification) result.getData();
        assertEquals(23, notification.getEventId().getSequenceNumber());
    }

    @Test
    public void roundtrip() throws ConversionException {
        this.serialize();
        this.deserialize();
    }

}
