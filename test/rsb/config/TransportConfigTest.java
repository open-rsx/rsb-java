package rsb.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.util.Properties;

/**
 * Test for {@link TransportConfig}.
 *
 * @author jwienke
 */
public class TransportConfigTest {

    @Test
    public void defaultConstruction() {
        final String name = "blaa";
        final TransportConfig config = new TransportConfig(name);
        assertEquals(name, config.getName());
        assertFalse(config.isEnabled());
        assertTrue(config.getOptions().getAvailableKeys().isEmpty());
    }

    @Test
    public void detailedConstruction() {
        final String name = "blaaba";
        final Properties props = new Properties();
        props.setProperty("fooxx", "bar");
        final TransportConfig config = new TransportConfig(name, true, props);
        assertEquals(name, config.getName());
        assertTrue(config.isEnabled());
        assertSame(props, config.getOptions());
    }

    @Test
    public void enabled() {
        final TransportConfig config = new TransportConfig("foo");
        config.setEnabled(true);
        assertTrue(config.isEnabled());
        config.setEnabled(false);
        assertFalse(config.isEnabled());
    }

    @Test
    public void options() {
        final TransportConfig config = new TransportConfig("narf");
        final Properties props = new Properties();
        props.setProperty("foofoo", "barz");
        assertNotSame(props, config.getOptions());
        config.setOptions(props);
        assertSame(props, config.getOptions());
    }

}
