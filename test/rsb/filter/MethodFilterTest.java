package rsb.filter;

import static org.junit.Assert.*;

import org.junit.Test;

import rsb.Event;

public class MethodFilterTest {

	@Test
	public void test() {
		MethodFilter filter = new MethodFilter("REQUEST");
		Event event = new Event();
		event.setMethod("REQUEST");
		assertNotNull(filter.transform(event));
		event.setMethod("REPLY");
		assertNull(filter.transform(event));
		event.setMethod("request");
		assertNotNull(filter.transform(event));
	}

}
