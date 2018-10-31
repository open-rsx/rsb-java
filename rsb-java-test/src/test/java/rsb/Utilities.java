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

import rsb.config.ParticipantConfig;
import rsb.config.ParticipantConfigCreator;
import rsb.config.TransportConfig;
import rsb.util.ConfigLoader;
import rsb.util.Properties;

/**
 * A class with test helpers.
 *
 * @author jwienke
 */
public final class Utilities {

    private static final String SOCKET_TRANSPORT_CONFIG_KEY = "socket";
    private static final String SOCKET_TRANSPORT_PORT_KEY =
            "transport.socket.port";
    private static final int SOCKET_PORT_OFFSET = 10;

    private Utilities() {
        super();
        // prevent initialization of helper class
    }

    /**
     * Creates a participant config to test participants which uses a reasonable
     * transport configured from the configuration file.
     *
     * @return participant config
     */
    public static ParticipantConfig createParticipantConfig() {

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(
                SOCKET_TRANSPORT_CONFIG_KEY).setEnabled(true);

        // handle configuration
        final Properties properties = new Properties();
        new ConfigLoader().loadEnv(properties);
        new ParticipantConfigCreator().reconfigure(config, properties);

        return config;

    }

    /**
     * Creates a participant config that will result in a failure in case a
     * client tries to use it because the transport will not connect.
     *
     * @return a participant configuration that fails connecting
     */
    public static ParticipantConfig createBrokenParticipantConfig() {
        final ParticipantConfig config = Utilities.createParticipantConfig();
        final TransportConfig socketConfig = config.getOrCreateTransport(
                SOCKET_TRANSPORT_CONFIG_KEY);
        socketConfig.getOptions().setProperty(
                SOCKET_TRANSPORT_PORT_KEY,
                Integer.toString(
                    socketConfig.getOptions().getProperty(
                        SOCKET_TRANSPORT_PORT_KEY).asInteger()
                    + SOCKET_PORT_OFFSET));
        socketConfig.getOptions().setProperty("transport.socket.server", "0");
        return config;
    }

}
