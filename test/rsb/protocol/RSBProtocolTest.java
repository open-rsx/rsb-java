/**
 * 
 */
package rsb.protocol;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import rsb.protocol.NotificationPB.Notification;

/**
 * @author swrede
 * 
 */
public class RSBProtocolTest {

	@Test
	public void testNotification() {
		Notification n1 = Notification.newBuilder().setId("1")
				.setWireSchema("String").setScope("blub").build();
		byte[] b = n1.toByteArray();
		Notification n2 = null;
		try {
			n2 = Notification.parseFrom(b);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			fail("InvalidProtocolBufferException");
		}
		assertNotNull(n1);
		assertNotNull(n2);
		assertTrue(n1.getId().equals(n2.getId()));
		assertTrue(n1.getWireSchema().equals(n2.getWireSchema()));
		assertTrue(n1.getScope().equals(n2.getScope()));
	}

}
