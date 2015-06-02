/**
 * ============================================================
 *
 * This file is part of the rsb-java project
 *
 * Copyright (C) 2015 CoR-Lab, Bielefeld University
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
package rsb.transport.spread;

import rsb.QualityOfServiceSpec;
import rsb.QualityOfServiceSpec.Ordering;
import rsb.QualityOfServiceSpec.Reliability;
import rsb.util.Properties;

/**
 * Utility class for parsing {@link rsb.config.ParticipantConfig} entries into
 * domain objects for the spread transport.
 *
 * @author jwienke
 */
final class ConfigParseUtilities {

    private static final String HOST_KEY = "transport.spread.host";
    private static final String DEFAULT_HOST = "localhost";
    private static final String PORT_KEY = "transport.spread.port";
    private static final String DEFAULT_PORT = "4803";
    private static final String NODELAY_KEY = "transport.spread.tcpnodelay";
    private static final String DEFAULT_NODELAY = "true";

    private static final String QOS_RELIABILITY_KEY =
            "qualityofservice.reliability";
    private static final String QOS_ORDERING_KEY = "qualityofservice.ordering";

    private ConfigParseUtilities() {
        // prevent utility class instantiation
    }

    /**
     * Extract {@link SpreadOptions} from a {@link Properties} instances.
     *
     * @param properties
     *            properties containing the configuration information with the
     *            full path prefix for the spread transport. Not
     *            <code>null</code>.
     * @return parsed {@link SpreadOptions} including default values if not
     *         specified by the properties
     */
    public static SpreadOptions spreadOptionsFromProperties(
            final Properties properties) {
        assert properties != null;
        return new SpreadOptions(properties.getProperty(HOST_KEY, DEFAULT_HOST)
                .asString(), properties.getProperty(PORT_KEY, DEFAULT_PORT)
                .asInteger(), properties.getProperty(NODELAY_KEY,
                DEFAULT_NODELAY).asBoolean());
    }

    /**
     * Parses a {@link QualityOfServiceSpec} instance from {@link Properties}.
     *
     * @param properties
     *            configuration properties, not <code>null</code>
     * @return parsed {@link QualityOfServiceSpec} instance with defaults for
     *         values not provided
     */
    public static QualityOfServiceSpec parseQualityOfServiceSpec(
            final Properties properties) {
        Ordering ordering = new QualityOfServiceSpec().getOrdering();
        if (properties.hasProperty(QOS_ORDERING_KEY)) {
            ordering =
                    Ordering.valueOf(properties.getProperty(QOS_ORDERING_KEY)
                            .asString());
        }
        Reliability reliability = new QualityOfServiceSpec().getReliability();
        if (properties.hasProperty(QOS_RELIABILITY_KEY)) {
            reliability =
                    Reliability.valueOf(properties.getProperty(
                            QOS_RELIABILITY_KEY).asString());
        }

        return new QualityOfServiceSpec(ordering, reliability);
    }

}
