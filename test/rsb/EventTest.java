package rsb;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Test case for {@link Event}.
 *
 * @author jwienke
 */
public class EventTest {

	@Test
	public void comparison() {

		Event event1 = new Event();
		Event event2 = new Event();
		event2.getMetaData()
				.setCreateTime(event1.getMetaData().getCreateTime());
		assertEquals(event1, event2);
		
//		// TODO add Id test
//		meta1.setSenderId(new Id());
//		assertFalse(meta1.equals(meta2));
//		assertFalse(meta2.equals(meta1));
//
//		meta2.setSenderId(meta1.getSenderId());
//		assertEquals(meta1, meta2); // identical
//
//		meta1.setSenderId(new Id());
//		assertFalse(meta1.equals(meta2)); // distinct UUIDs, again		

		event1.setScope(new Scope("/foo/bar"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setScope(new Scope("/other/scope"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setScope(event1.getScope());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));

		event1.setType("blubb".getClass());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setType(Boolean.class);
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setType(event1.getType());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));

		event1.setData(new ArrayList<Integer>());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setData("no match data");
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setData(event1.getData());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));

		event1.setId(new ParticipantId(), 23);
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setId(event1.getId().getParticipantId(), event1.getId().getSequenceNumber());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));
		
		event1.setMethod("REQUEST");
		assertFalse(event1.equals(event2));
		event2.setMethod("Blub");
		assertFalse(event1.equals(event2));
		event2.setMethod("request");
		assertTrue(event1.equals(event2));
		
		// causes
		EventId cause1 = new EventId(new ParticipantId(), 343);
		event1.addCause(cause1);
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.addCause(cause1);
		assertEquals(event1, event2);

	}
	
	@Test
	public void testHashCode() {
		
		Event event1 = new Event();
		Event event2 = new Event();
		assertEquals(event1.hashCode(), event2.hashCode());
		
		// causes
		event1.addCause(new EventId(new ParticipantId(), 234234));
		assertFalse(event1.hashCode() == event2.hashCode());
		
	}

}
