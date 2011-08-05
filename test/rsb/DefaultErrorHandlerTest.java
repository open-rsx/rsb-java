package rsb;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class DefaultErrorHandlerTest {

	private final static Logger LOG = Logger.getLogger(DefaultErrorHandlerTest.class.getName());
	
	@Test
	public void testError() {
		DefaultErrorHandler handler = new DefaultErrorHandler(LOG);
		assertNotNull(handler);
		RSBException except = new RSBException(new RuntimeException("test"));
		handler.warning(except);
		handler.error(except);
	}

}
