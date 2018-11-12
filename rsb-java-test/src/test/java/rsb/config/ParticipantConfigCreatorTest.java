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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.RsbTestCase;
import rsb.util.Properties;

/**
 * Test for {@link ParticipantConfigCreator}.
 *
 * @author jwienke
 */
public class ParticipantConfigCreatorTest extends RsbTestCase {

    private static final String FALSE_STRING = "false";
    private static final String SOCKET_TRANSPORT = "socket";

    @Test
    public void reconfigurePreservesEnabled() {

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SOCKET_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, new Properties());

        assertTrue(config.getTransports().get(SOCKET_TRANSPORT).isEnabled());

    }

    @Test
    public void enable() {

        final Properties props = new Properties();
        props.setProperty("transport.socket.enabled", FALSE_STRING);

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SOCKET_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, props);

        assertFalse(config.getTransports().get(SOCKET_TRANSPORT).isEnabled());

    }

    @Test
    public void options() {

        final Properties props = new Properties();
        final String key1 = "transport.socket.foo";
        final String randomValue = "42";
        props.setProperty(key1, randomValue);
        final String key2 = "transport.test";
        props.setProperty(key2, randomValue);
        final String key3 = "transport.blubb.nooo";
        props.setProperty(key3, randomValue);

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SOCKET_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, props);

        assertTrue(config.getTransports().get(SOCKET_TRANSPORT).getOptions()
                .hasProperty(key1));
        assertFalse(config.getTransports().get(SOCKET_TRANSPORT).getOptions()
                .hasProperty(key2));
        assertFalse(config.getTransports().get(SOCKET_TRANSPORT).getOptions()
                .hasProperty(key3));

    }

    @Test
    public void introspection() {

        final ParticipantConfig config = new ParticipantConfig();
        final Properties props = new Properties();
        new ParticipantConfigCreator().reconfigure(config, props);
        assertTrue(config.isIntrospectionEnabled());

        props.setProperty("introspection.enabled", FALSE_STRING);
        new ParticipantConfigCreator().reconfigure(config, props);
        assertFalse(config.isIntrospectionEnabled());

    }

    @Test
    public void configuresUnknownTransports() throws Exception {
        final Properties props = new Properties();
        final String key = "transport.strange.enabled";
        final String value = "true";
        props.setProperty(key, value);

        final ParticipantConfig config = new ParticipantConfig();
        config.getOrCreateTransport(SOCKET_TRANSPORT).setEnabled(true);
        new ParticipantConfigCreator().reconfigure(config, props);

        assertTrue(config.getTransports().get("strange").isEnabled());
    }

}
