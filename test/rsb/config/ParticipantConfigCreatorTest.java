package rsb.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import rsb.transport.DefaultTransports;
import rsb.transport.TransportRegistry;
import rsb.util.Properties;

/**
 * Test for {@link ParticipantConfigCreator}.
 *
 * @author jwienke
 */
public class ParticipantConfigCreatorTest {

    private static final String SPREAD_TRANSPORT = "spread";

    @BeforeClass
    public static void registerDefaultTransports() {
        DefaultTransports.register();
    }

    @Test
    public void createContainsAllTransports() {

        final ParticipantConfig config = new ParticipantConfigCreator()
                .create(new Properties());
        for (final String transportName : TransportRegistry
                .getDefaultInstance().transportNames()) {
            assertTrue(config.hasTransport(transportName));
        }

    }

    @Test
    public void reconfigurePreservesEnabled() {

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SPREAD_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, new Properties());

        assertTrue(config.getTransports().get(SPREAD_TRANSPORT).isEnabled());

    }

    @Test
    public void enable() {

        final Properties props = new Properties();
        props.setProperty("transport.spread.enabled", "false");

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SPREAD_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, props);

        assertFalse(config.getTransports().get(SPREAD_TRANSPORT).isEnabled());

    }

    @Test
    public void options() {

        final Properties props = new Properties();
        final String key1 = "transport.spread.foo";
        final String randomValue = "42";
        props.setProperty(key1, randomValue);
        final String key2 = "transport.test";
        props.setProperty(key2, randomValue);
        final String key3 = "transport.blubb.nooo";
        props.setProperty(key3, randomValue);

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SPREAD_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, props);

        assertTrue(config.getTransports().get(SPREAD_TRANSPORT).getOptions()
                .hasProperty(key1));
        assertFalse(config.getTransports().get(SPREAD_TRANSPORT).getOptions()
                .hasProperty(key2));
        assertFalse(config.getTransports().get(SPREAD_TRANSPORT).getOptions()
                .hasProperty(key3));

    }

}
