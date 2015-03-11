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

    private static final String FALSE_STRING = "false";
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
        props.setProperty("transport.spread.enabled", FALSE_STRING);

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

}
