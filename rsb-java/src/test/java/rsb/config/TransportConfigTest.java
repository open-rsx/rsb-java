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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsb.RsbTestCase;
import rsb.util.Properties;

/**
 * Test for {@link TransportConfig}.
 *
 * @author jwienke
 */
public class TransportConfigTest extends RsbTestCase {

    @Test
    public void defaultConstruction() {
        final String name = "blaa";
        final TransportConfig config = new TransportConfig(name);
        assertEquals(name, config.getName());
        assertFalse(config.isEnabled());
        assertTrue(config.getOptions().getAvailableKeys().isEmpty());
    }

    @Test
    public void detailedConstruction() {
        final String name = "blaaba";
        final Properties props = new Properties();
        props.setProperty("fooxx", "bar");
        final TransportConfig config = new TransportConfig(name, true, props);
        assertEquals(name, config.getName());
        assertTrue(config.isEnabled());
        assertSame(props, config.getOptions());
    }

    @Test
    public void enabled() {
        final TransportConfig config = new TransportConfig("foo");
        config.setEnabled(true);
        assertTrue(config.isEnabled());
        config.setEnabled(false);
        assertFalse(config.isEnabled());
    }

    @Test
    public void options() {
        final TransportConfig config = new TransportConfig("narf");
        final Properties props = new Properties();
        props.setProperty("foofoo", "barz");
        assertNotSame(props, config.getOptions());
        config.setOptions(props);
        assertSame(props, config.getOptions());
    }

}
