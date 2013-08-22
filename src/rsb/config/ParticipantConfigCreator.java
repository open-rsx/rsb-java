/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2013 CoR-Lab, Bielefeld University
 *
 * This file may be licensed under the terms of the
 * GNU Lesser General Public License Version 3 (the ``LGPL''),
 * or (at your option) any later version.
 *
 * Software distributed under the License is distributed
 * on an ``AS IS'' basis, WITHOUT WARRANTY OF ANY KIND, either
 * express or implied. See the LGPL for the specific language
 * governing rights and limitations.
 *
 * You should have received a copy of the LGPL along with this
 * program. If not, go to http://www.gnu.org/licenses/lgpl.html
 * or write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * The development of this software was supported by:
 *   CoR-Lab, Research Institute for Cognition and Robotics
 *     Bielefeld University
 *
 * ============================================================
 */
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
