package rsb.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link ParticipantConfig}.
 *
 * @author jwienke
 */
public class ParticipantConfigTest {

    @Test
    public void construction() {
        final ParticipantConfig config = new ParticipantConfig();
        assertTrue(config.getTransports().isEmpty());
    }

    @Test
    public void hasTransport() {
        final ParticipantConfig config = new ParticipantConfig();
        final String name = "bla";
        assertFalse(config.hasTransport(name));
        config.getOrCreateTransport(name);
        assertTrue(config.hasTransport(name));
    }

    @Test
    public void getEnabledTransports() {
        final ParticipantConfig config = new ParticipantConfig();
        assertTrue(config.getEnabledTransports().isEmpty());

        final String name = "bla";
        config.getOrCreateTransport(name);
        assertTrue(config.getEnabledTransports().isEmpty());

        config.getOrCreateTransport(name).setEnabled(true);
        assertFalse(config.getEnabledTransports().isEmpty());

    }

}
