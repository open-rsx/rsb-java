/**
 * 
 */
package rsb.protocol;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import rsb.protocol.NotificationPB.Notification;


/**
 * @author swrede
 *
 */
public class RSBProtocolTest {
	
	private final static Logger log = Logger.getLogger(RSBProtocolTest.class.getName());	
	
	@Test
	public void testNotification() {
		Notification n1 = Notification.newBuilder().setEid("1").setTypeId("String").setUri("blub").setStandalone(true).build();
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
		assertTrue(n1.getEid().equals(n2.getEid()));
		assertTrue(n1.getTypeId().equals(n2.getTypeId()));
		assertTrue(n1.getUri().equals(n2.getUri()));
	}

}
