package rsb.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

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

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void filter() {

        final Properties props = new Properties();

        // add some options which we want to retrieve later
        final String desiredPrefix = "a.test";
        final String[] desired = new String[] { desiredPrefix + ".foo",
                desiredPrefix + ".bar.bla", desiredPrefix };
        for (final String key : desired) {
            props.setProperty(key, "blaaa");
        }

        // add some stuff we do not want
        for (final String key : new String[] { "a", "other.root", "bar.bla",
                "a.testing" }) {
            props.setProperty(key, "blaaa");
        }

        final Properties filtered = props.filter(desiredPrefix);
        assertEquals(new HashSet<String>(Arrays.asList(desired)),
                filtered.getAvailableKeys());

    }

    @Test
    public void merge() {

        final Properties target = new Properties();
        target.setProperty(KEY, VALUE);

        final String notExistingKey = "Ido.not.exist";
        final String notExistingValue = "gjhfghsFA";
        final String duplicatedValue = "567567fghfdgser";
        final Properties toMerge = new Properties();
        toMerge.setProperty(KEY, duplicatedValue);
        toMerge.setProperty(notExistingKey, notExistingValue);

        target.merge(toMerge);

        assertTrue(target.hasProperty(KEY));
        assertTrue(target.hasProperty(notExistingKey));
        assertEquals(duplicatedValue, target.getProperty(KEY).asString());
        assertEquals(notExistingValue, target.getProperty(notExistingKey)
                .asString());

    }

}
