package rsb;

import java.io.File;

import rsb.config.ParticipantConfig;
import rsb.config.ParticipantConfigCreator;
import rsb.transport.spread.SpreadWrapper;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

/**
 * A class with helpers for testing the spread transport.
 *
 * @author jwienke
 */
public final class Utilities {

    private Utilities() {
        super();
        // prevent initialization of helper class
    }

    /**
     * Creates a {@link SpreadWrapper} instance while respecting the RSB global
     * configuration.
     *
     * @return new instance
     * @throws Throwable
     *             error configuring
     */
    public static SpreadWrapper createSpreadWrapper() throws Throwable {
        final ConfigLoader loader = new ConfigLoader();
        return new SpreadWrapper("localhost", loader.load(new Properties())
                .getProperty("transport.spread.port", "4803").asInteger(), true);
    }

    /**
     * Creates a participant config to test participants which uses a reasonable
     * transport configured from the configuration file.
     *
     * @return participant config
     */
    public static ParticipantConfig createParticipantConfig() {

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport("socket").setEnabled(true);

        // handle configuration
        final Properties properties = new Properties();
        new ConfigLoader().loadFileIfAvailable(
                new File(System.getProperty("user.dir") + "/rsb.conf"),
                properties);
        new ParticipantConfigCreator().reconfigure(config, properties);

        return config;

    }

}
