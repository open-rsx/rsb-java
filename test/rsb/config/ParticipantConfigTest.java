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

        final String name = "narf";
        config.getOrCreateTransport(name);
        assertTrue(config.getEnabledTransports().isEmpty());

        config.getOrCreateTransport(name).setEnabled(true);
        assertFalse(config.getEnabledTransports().isEmpty());

    }

    @Test
    public void introspectionEnabled() {
        final ParticipantConfig config = new ParticipantConfig();
        assertFalse(config.isIntrospectionEnabled());
        config.setIntrospectionEnabled(true);
        assertTrue(config.isIntrospectionEnabled());
    }
}
