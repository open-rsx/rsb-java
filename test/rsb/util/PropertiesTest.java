package rsb.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertiesTest {

	@Test
	public void testGetInstance() {
		Properties prop = Properties.getInstance();
		assertTrue(prop.getProperty("RSB.Version").equalsIgnoreCase("0.8"));
	}

	@Test
	public void testSetProperty() {
		Properties prop = Properties.getInstance();
		prop.setProperty("transport.spread.port", "8888");
		assertTrue(prop.getPropertyAsInt("transport.spread.port")==8888);
	}

}
