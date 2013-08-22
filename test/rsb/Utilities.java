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
