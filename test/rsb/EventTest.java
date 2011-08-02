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

		event1.setScope(new Scope("/foo/bar"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setScope(new Scope("/other/scope"));
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setScope(event1.getScope());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));

		event1.setType("blubb");
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.setType("no match type");
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

		event1.getMetaData().setSenderId(new Id());
		assertFalse(event1.equals(event2));
		assertFalse(event2.equals(event1));
		event2.getMetaData().setSenderId(event1.getMetaData().getSenderId());
		assertTrue(event1.equals(event2));
		assertTrue(event2.equals(event1));

	}

}
