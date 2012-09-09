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
import java.util.logging.Logger;

import org.junit.Test;

import rsb.ParticipantId;
import rsb.protocol.NotificationType.Notification;
import rsb.protocol.EventIdType.EventId;

import com.google.protobuf.ByteString;

/**
 * @author swrede
 */
public class ProtocolBufferConverterTest {

	final static Logger LOG = Logger
			.getLogger(ProtocolBufferConverterTest.class.getName());

	WireContents<ByteBuffer> buffer;
	ProtocolBufferConverter<Notification> converter = new ProtocolBufferConverter<Notification>(
			Notification.getDefaultInstance());

	/**
	 * Test method for
	 * {@link rsb.converter.ProtocolBufferConverter#serialize(java.lang.String, java.lang.Object)}
	 * .
	 *
	 * @throws ConversionException
	 */
	public void testSerialize() throws ConversionException {
		Notification.Builder notificationBuilder = Notification.newBuilder();
		// notification metadata
		EventId.Builder eventIdBuilder = EventId.newBuilder();
		eventIdBuilder.setSequenceNumber(23);
		eventIdBuilder.setSenderId(ByteString.copyFrom(new ParticipantId()
				.toByteArray()));
		notificationBuilder.setEventId(eventIdBuilder);
		notificationBuilder.setWireSchema(ByteString
				.copyFromUtf8("rsb.notification"));
		notificationBuilder.setScope(ByteString.copyFromUtf8("rsb"));

		buffer = converter.serialize(Notification.class,
				notificationBuilder.build());
		assertNotNull(buffer);
	}

	/**
	 * Test method for
	 * {@link rsb.converter.ProtocolBufferConverter#deserialize(java.lang.String, java.nio.ByteBuffer)}
	 * .
	 *
	 * @throws ConversionException
	 */
	public void testDeserialize() throws ConversionException {
		assertNotNull(buffer);
		UserData<Notification> result = converter.deserialize(
				".rsb.protocol.Notification", buffer.getSerialization());
		Notification n = (Notification) result.getData();
		assertEquals(23, n.getEventId().getSequenceNumber());
	}

	@Test
	public void testRoundtrip() throws ConversionException {
		testSerialize();
		testDeserialize();
	}

}
