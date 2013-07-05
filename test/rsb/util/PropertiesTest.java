package rsb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class PropertiesTest {

    @After
    public void resetProperties() {
        Properties.getInstance().reset();
    }

    @Test
    public void getInstance() {
        final Properties prop = Properties.getInstance();
        assertNotNull(prop);
    }

    @Test
    public void setProperty() {
        final Properties prop = Properties.getInstance();
        prop.setProperty("Spread.Path", "8888");
        assertEquals("8888", prop.getProperty("Spread.Path"));
    }

    @Test
    public void setPortPropertyTwice() {
        final Properties prop = Properties.getInstance();
        prop.setProperty("transport.spread.port", "8888");
        assertTrue(prop.getPropertyAsInt("transport.spread.port") == 8888);
        prop.setProperty("transport.spread.port", "8888");
        assertTrue(prop.getPropertyAsInt("transport.spread.port") == 8888);
    }

    @Test
    public void setHostPropertyTwice() {
        final Properties prop = Properties.getInstance();
        prop.setProperty("transport.spread.host", "11.1.1.11");
        assertTrue(prop.getProperty("transport.spread.host").equalsIgnoreCase(
                "11.1.1.11"));
        prop.setProperty("transport.spread.host", "11.1.1.11");
        assertTrue(prop.getProperty("transport.spread.host").equalsIgnoreCase(
                "11.1.1.11"));
    }

    @Test
    public void reset() {
        final Properties prop = Properties.getInstance();
        prop.setProperty("transport.spread.host", "11.1.1.11");
        assertTrue(prop.getProperty("transport.spread.host").equalsIgnoreCase(
                "11.1.1.11"));
        prop.reset();
        assertTrue(prop.getProperty("transport.spread.host").equalsIgnoreCase(
                "localhost"));
    }

}
