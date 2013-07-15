package rsb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertiesTest {

    private static final String KEY = "Spread.Path";
    private static final String VALUE = "blaaaa";

    @Test
    public void setGetProperty() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        assertEquals(VALUE, prop.getProperty(KEY).asString());
    }

    @Test
    public void getDefault() {
        final Properties prop = new Properties();
        final String def = "blaablaa";
        assertEquals(def, prop.getProperty(KEY, def).asString());
    }

    @Test
    public void setPortPropertyTwice() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        assertEquals(VALUE, prop.getProperty(KEY).asString());
        final String value2 = VALUE + "test";
        prop.setProperty(KEY, value2);
        assertEquals(value2, prop.getProperty(KEY).asString());
    }

    @Test
    public void reset() {
        final Properties prop = new Properties();
        prop.setProperty(KEY, VALUE);
        prop.reset();
        assertTrue(prop.getAvailableKeys().isEmpty());
        assertFalse(prop.hasProperty(KEY));
    }

    @Test
    public void defaultValueNumber() {
        final Properties prop = new Properties();
        final int defaultValue = 42;
        assertEquals(defaultValue, prop.getProperty(KEY, defaultValue)
                .asInteger());
    }

    @Test
    public void defaultValueBoolean() {
        final Properties prop = new Properties();
        assertTrue(prop.getProperty(KEY, true).asBoolean());
        assertFalse(prop.getProperty(KEY, false).asBoolean());
    }

}
