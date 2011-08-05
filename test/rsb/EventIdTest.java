package rsb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventIdTest {
	
	ParticipantId participantId = new ParticipantId();

	@Test
	public void testEqualsObject() {
		EventId eventId_a = new EventId(participantId, 0);
		EventId eventId_b = new EventId(participantId, 0);
		assertEquals(eventId_a, eventId_b);
		assertTrue(eventId_a.getAsUUID().equals(eventId_b.getAsUUID()));
		eventId_b = new EventId(participantId, 1);
		assertFalse(eventId_a.getAsUUID().equals(eventId_b.getAsUUID()));	
	}

	@Test
	public void testGetAsUUID() {
		EventId eventId = new EventId(participantId, 1);
		assertNotNull(eventId.getAsUUID());
	}
	
}
