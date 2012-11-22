package rsb.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertiesTest {

	@Test
	public void testGetInstance() {
		Properties prop = Properties.getInstance();
		assertNotNull(prop);
	}

	@Test
	public void testSetProperty() {
		Properties prop = Properties.getInstance();
		prop.setProperty("Spread.Path", "8888");
		assertEquals("8888", prop.getProperty("Spread.Path"));
	}

}
