package rsb.config;

import java.util.HashSet;
import java.util.Set;

import rsb.transport.TransportRegistry;
import rsb.util.Properties;

/**
 * Creates {@link ParticipantConfig} instances from configuration
 * {@link Properties} instances.
 *
 * @author jwienke
 */
public class ParticipantConfigCreator {

    /**
     * Create a new {@link ParticipantConfig} from the given properties.
     *
     * @param properties
     *            properties to use
     * @return the new instance
     */
    public ParticipantConfig create(final Properties properties) {

        final ParticipantConfig config = new ParticipantConfig();
        reconfigure(config, properties);
        return config;

    }

    /**
     * Reconfigures an existing {@link ParticipantConfig} with options from a
     * {@link Properties} object. This means that all options found in the
     * properties object either override the existing ones in the participant
     * config or are added in case they did not exist.
     *
     * @param config
     *            the config to modify
     * @param properties
     *            the properties to use
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void reconfigure(final ParticipantConfig config,
            final Properties properties) {

        for (final String transportName : TransportRegistry
                .getDefaultInstance().transportNames()) {

            final String transportPropPrefix = "transport." + transportName;
            final Properties transportProps = properties
                    .filter(transportPropPrefix);

            final Set<String> handledKeys = new HashSet<String>();

            // find or create a new TransportConfig instance
            final TransportConfig transportConfig = config
                    .getOrCreateTransport(transportName);

            // handle enabled
            // CHECKSTYLE.OFF: AvoidNestedBlocks - limit scope of the one
            // options
            {
                final String enabledKey = transportPropPrefix + ".enabled";
                transportConfig.setEnabled(transportProps.getProperty(
                        enabledKey, transportConfig.isEnabled()).asBoolean());
                handledKeys.add(enabledKey);
            }
            // CHECKSTYLE.ON: AvoidNestedBlocks

            // pass remaining options to the transport config
            transportProps.remove(handledKeys);
            transportConfig.getOptions().merge(transportProps);

        }

    }
}
